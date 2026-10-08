package echo.music.iad1tya.ui.screens.search.suggestions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.music.innertube.YouTube
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import dagger.hilt.android.lifecycle.HiltViewModel
import echo.music.iad1tya.playback.PlayerConnection
import echo.music.iad1tya.playback.queues.YouTubeQueue
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class SuggestionsViewModel @Inject constructor() : ViewModel() {
  private var currentLoadedRegion: String? = null

  private val _suggestionTracks = MutableStateFlow<List<SuggestionTrack>?>(null)
  val suggestionTracks: StateFlow<List<SuggestionTrack>?> = _suggestionTracks

  private val _suggestionArtists = MutableStateFlow<List<SuggestionArtist>?>(null)
  val suggestionArtists: StateFlow<List<SuggestionArtist>?> = _suggestionArtists

  private val _suggestionAlbums = MutableStateFlow<List<SuggestionAlbum>?>(null)
  val suggestionAlbums: StateFlow<List<SuggestionAlbum>?> = _suggestionAlbums

  private val _suggestionVideos = MutableStateFlow<List<SuggestionTrack>?>(null)
  val suggestionVideos: StateFlow<List<SuggestionTrack>?> = _suggestionVideos

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading

  private val _isManualLoading = MutableStateFlow(false)
  val isManualLoading: StateFlow<Boolean> = _isManualLoading

  fun refresh(countryCode: String = "system", force: Boolean = false) {
    val resolvedCode =
      if (countryCode == "system") {
        java.util.Locale.getDefault().country.lowercase()
      } else {
        countryCode.lowercase()
      }

    if (_isLoading.value && !force && currentLoadedRegion == resolvedCode) return

    viewModelScope.launch(Dispatchers.IO) {
      _isLoading.value = true
      if (force) _isManualLoading.value = true

      if (currentLoadedRegion != resolvedCode || force) {
        _suggestionTracks.value = null
        _suggestionArtists.value = null
        _suggestionAlbums.value = null
        _suggestionVideos.value = null
      }

      try {
        coroutineScope {
          // 1. Fetch Charts from YouTube Music InnerTube
          launch {
            try {
              val chartsResult = YouTube.getChartsPage().getOrNull()
              if (chartsResult != null) {
                val chartSongs = chartsResult.sections
                  .flatMap { it.items }
                  .filterIsInstance<SongItem>()
                  .distinctBy { it.id }

                if (chartSongs.isNotEmpty()) {
                  val mappedTracks = chartSongs.take(30).mapIndexed { idx, song ->
                    SuggestionTrack(
                      rank = idx + 1,
                      title = song.title,
                      artist = song.artists.joinToString(", ") { it.name },
                      thumbnailUrl = song.thumbnail,
                      id = song.id
                    )
                  }
                  _suggestionTracks.value = mappedTracks

                  // Artists from YouTube chart songs
                  val rawArtists = chartSongs.flatMap { song ->
                    song.artists.map { artist -> artist.name to artist.id }
                  }.distinctBy { it.first }

                  val mappedArtists = rawArtists.take(15).mapIndexed { idx, (name, id) ->
                    SuggestionArtist(
                      rank = idx + 1,
                      name = name,
                      thumbnailUrl = null,
                      id = id
                    )
                  }
                  _suggestionArtists.value = mappedArtists

                  // Fetch real artist portrait thumbnails concurrently in background
                  launch(Dispatchers.IO) {
                    try {
                      val enriched = mappedArtists.map { a ->
                        async {
                          try {
                            val ytArtist = YouTube.search(a.name, YouTube.SearchFilter.FILTER_ARTIST).getOrNull()
                              ?.items?.filterIsInstance<ArtistItem>()?.firstOrNull()
                            if (ytArtist != null) {
                              a.copy(
                                thumbnailUrl = ytArtist.thumbnail,
                                id = ytArtist.id.ifBlank { a.id }
                              )
                            } else {
                              a
                            }
                          } catch (_: Exception) {
                            a
                          }
                        }
                      }.awaitAll()
                      _suggestionArtists.value = enriched
                    } catch (_: Exception) {}
                  }

                  // Trending videos from music video songs
                  val videoSongs = chartSongs.filter { it.musicVideoType != null }.ifEmpty { chartSongs.drop(10) }
                  if (videoSongs.isNotEmpty()) {
                    _suggestionVideos.value = videoSongs.take(10).mapIndexed { idx, song ->
                      SuggestionTrack(
                        rank = idx + 1,
                        title = song.title,
                        artist = song.artists.joinToString(", ") { it.name },
                        thumbnailUrl = song.thumbnail,
                        id = song.id
                      )
                    }
                  }
                }
              }
            } catch (e: Exception) {
              Log.e("SuggestionsViewModel", "Failed to fetch YouTube charts", e)
            }
          }

          // 2. Fetch Trending / New Release Albums from YouTube Music InnerTube
          launch {
            try {
              val explore = YouTube.explore().getOrNull()
              val albums = explore?.newReleaseAlbums?.ifEmpty { null }
                ?: YouTube.newReleaseAlbums().getOrNull()

              if (!albums.isNullOrEmpty()) {
                val mappedAlbums = albums.distinctBy { it.id }.take(15).mapIndexed { idx, album ->
                  SuggestionAlbum(
                    rank = idx + 1,
                    title = album.title,
                    artist = album.artists?.joinToString(", ") { it.name } ?: "",
                    thumbnailUrl = album.thumbnail,
                    id = album.id
                  )
                }
                _suggestionAlbums.value = mappedAlbums
              }
            } catch (e: Exception) {
              Log.e("SuggestionsViewModel", "Failed to fetch YouTube albums", e)
            }
          }
        }

        currentLoadedRegion = resolvedCode
      } catch (e: Exception) {
        Log.e("SuggestionsViewModel", "Failed to fetch suggestions", e)
      } finally {
        _isLoading.value = false
        _isManualLoading.value = false
      }
    }
  }

  fun playTrack(track: SuggestionTrack, playerConnection: PlayerConnection?) {
    val trackId = track.id
    if (!trackId.isNullOrBlank()) {
      playerConnection?.playQueue(YouTubeQueue(WatchEndpoint(videoId = trackId)))
      return
    }
    viewModelScope.launch(Dispatchers.IO) {
      val query = "${track.title} ${track.artist}"
      YouTube.search(query, YouTube.SearchFilter.FILTER_SONG).onSuccess { searchResult ->
        val songs = searchResult.items.filterIsInstance<SongItem>()
        val match = songs.firstOrNull()
        if (match != null) {
          withContext(Dispatchers.Main) {
            playerConnection?.playQueue(YouTubeQueue(WatchEndpoint(videoId = match.id)))
          }
        }
      }
    }
  }

  fun navigateToArtist(artist: SuggestionArtist, navController: NavController) {
    val artistId = artist.id
    if (!artistId.isNullOrBlank()) {
      navController.navigate("artist/$artistId")
      return
    }
    viewModelScope.launch(Dispatchers.IO) {
      YouTube.search(artist.name, YouTube.SearchFilter.FILTER_ARTIST).onSuccess { searchResult ->
        val firstArtist = searchResult.items.filterIsInstance<ArtistItem>().firstOrNull()
        if (firstArtist != null) {
          withContext(Dispatchers.Main) { navController.navigate("artist/${firstArtist.id}") }
        }
      }
    }
  }

  fun navigateToAlbum(album: SuggestionAlbum, navController: NavController) {
    val albumId = album.id
    if (!albumId.isNullOrBlank()) {
      navController.navigate("album/$albumId")
      return
    }
    viewModelScope.launch(Dispatchers.IO) {
      val query = "${album.title} ${album.artist}"
      YouTube.search(query, YouTube.SearchFilter.FILTER_ALBUM).onSuccess { searchResult ->
        val firstAlbum =
          searchResult.items.filterIsInstance<AlbumItem>().firstOrNull()
        if (firstAlbum != null) {
          withContext(Dispatchers.Main) { navController.navigate("album/${firstAlbum.id}") }
        }
      }
    }
  }

  fun playVideo(video: SuggestionTrack, playerConnection: PlayerConnection?) {
    val videoId = video.id
    if (!videoId.isNullOrBlank()) {
      playerConnection?.playQueue(YouTubeQueue(WatchEndpoint(videoId = videoId)))
      return
    }
    viewModelScope.launch(Dispatchers.IO) {
      val query = "${video.title} ${video.artist}"
      YouTube.search(query, YouTube.SearchFilter.FILTER_SONG).onSuccess { searchResult ->
        val songs = searchResult.items.filterIsInstance<SongItem>()
        val match = songs.firstOrNull()
        if (match != null) {
          withContext(Dispatchers.Main) {
            playerConnection?.playQueue(YouTubeQueue(WatchEndpoint(videoId = match.id)))
          }
        }
      }
    }
  }
}

