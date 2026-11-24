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
          launch {
            try {
              val tracks = AppleMusicScraper.fetchTopSongs(resolvedCode)
              if (tracks.isNotEmpty()) {
                _suggestionTracks.value = tracks
                _suggestionArtists.value = AppleMusicScraper.getTrendingArtists(tracks)
              }
            } catch (e: Exception) {
              Log.e("SuggestionsViewModel", "Failed to fetch songs", e)
            }
          }

          launch {
            try {
              val albums = AppleMusicScraper.fetchTopAlbums(resolvedCode)
              if (albums.isNotEmpty()) {
                _suggestionAlbums.value = albums
              }
            } catch (e: Exception) {
              Log.e("SuggestionsViewModel", "Failed to fetch albums", e)
            }
          }

          launch {
            try {
              val videos = AppleMusicScraper.fetchTopVideos(resolvedCode)
              if (videos.isNotEmpty()) {
                _suggestionVideos.value = videos
              }
            } catch (e: Exception) {
              Log.e("SuggestionsViewModel", "Failed to fetch videos", e)
            }
          }
        }

        currentLoadedRegion = resolvedCode

        // Apple Music scraping is best-effort and breaks whenever their markup changes. If a rail
        // came back empty, top it up from the YouTube Music home feed so the search landing screen
        // is never reduced to a single lonely section.
        if (_suggestionTracks.value.isNullOrEmpty() ||
          _suggestionArtists.value.isNullOrEmpty() ||
          _suggestionAlbums.value.isNullOrEmpty()
        ) {
          try {
            fillFromYouTube()
          } catch (e: Exception) {
            Log.e("SuggestionsViewModel", "YouTube fallback failed", e)
          }
        }
      } catch (e: Exception) {
        Log.e("SuggestionsViewModel", "Failed to fetch suggestions", e)
      } finally {
        _isLoading.value = false
        _isManualLoading.value = false
      }
    }
  }

  /**
   * Builds the trending rails from the YouTube Music home feed. Used as a fallback so the search
   * landing screen always has real, playable content.
   */
  private suspend fun fillFromYouTube() {
    val home = YouTube.home().getOrNull() ?: return

    val songs = LinkedHashMap<String, SongItem>()
    val albums = LinkedHashMap<String, AlbumItem>()
    val artists = LinkedHashMap<String, ArtistItem>()
    home.sections.forEach { section ->
      section.items.forEach { item ->
        when (item) {
          is SongItem -> songs.putIfAbsent(item.id, item)
          is AlbumItem -> albums.putIfAbsent(item.id, item)
          is ArtistItem -> artists.putIfAbsent(item.id, item)
          else -> {}
        }
      }
    }

    if (_suggestionTracks.value.isNullOrEmpty() && songs.isNotEmpty()) {
      _suggestionTracks.value =
        songs.values.take(30).mapIndexed { index, song ->
          SuggestionTrack(
            rank = index + 1,
            title = song.title,
            artist = song.artists.joinToString(", ") { it.name },
            thumbnailUrl = song.thumbnail,
          )
        }
    }

    if (_suggestionArtists.value.isNullOrEmpty() && artists.isNotEmpty()) {
      _suggestionArtists.value =
        artists.values.take(12).mapIndexed { index, artist ->
          SuggestionArtist(rank = index + 1, name = artist.title, thumbnailUrl = artist.thumbnail)
        }
    }

    if (_suggestionAlbums.value.isNullOrEmpty() && albums.isNotEmpty()) {
      _suggestionAlbums.value =
        albums.values.take(12).mapIndexed { index, album ->
          SuggestionAlbum(
            rank = index + 1,
            title = album.title,
            artist = album.artists?.joinToString(", ") { it.name } ?: "",
            thumbnailUrl = album.thumbnail,
          )
        }
    }
  }

  private fun artistMatches(ytArtistName: String, appleMusicArtist: String): Boolean {
    val ytNorm = ytArtistName.trim().lowercase()
    val apNorm = appleMusicArtist.trim().lowercase()
    // Check both directions — no stripping, no word removal
    return apNorm.contains(ytNorm) || ytNorm.contains(apNorm)
  }

  fun playTrack(track: SuggestionTrack, playerConnection: PlayerConnection?) {
    viewModelScope.launch(Dispatchers.IO) {
      val query = "${track.title} ${track.artist}"
      YouTube.search(query, YouTube.SearchFilter.FILTER_SONG).onSuccess { searchResult ->
        val songs = searchResult.items.filterIsInstance<SongItem>()

        val bestMatch =
          songs.firstOrNull { s ->
            s.title.equals(track.title, ignoreCase = true) &&
              s.artists.any { a -> artistMatches(a.name, track.artist) }
          }
            ?: songs.firstOrNull { s ->
              s.title.contains(track.title, ignoreCase = true) &&
                s.artists.any { a -> artistMatches(a.name, track.artist) }
            }
            ?: songs.firstOrNull { s -> s.artists.any { a -> artistMatches(a.name, track.artist) } }
            ?: songs.firstOrNull()

        if (bestMatch != null) {
          withContext(Dispatchers.Main) {
            playerConnection?.playQueue(YouTubeQueue(WatchEndpoint(videoId = bestMatch.id)))
          }
        }
      }
    }
  }

  fun navigateToArtist(artist: SuggestionArtist, navController: NavController) {
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
    viewModelScope.launch(Dispatchers.IO) {
      val query = "${album.title} ${album.artist}"
      YouTube.search(query, YouTube.SearchFilter.FILTER_ALBUM).onSuccess { searchResult ->
        val firstAlbum =
          searchResult.items.filterIsInstance<com.music.innertube.models.AlbumItem>().firstOrNull()
        if (firstAlbum != null) {
          withContext(Dispatchers.Main) { navController.navigate("album/${firstAlbum.id}") }
        }
      }
    }
  }

  fun playVideo(video: SuggestionTrack, playerConnection: PlayerConnection?) {
    viewModelScope.launch(Dispatchers.IO) {
      val query = "${video.title} ${video.artist}"
      YouTube.search(query, YouTube.SearchFilter.FILTER_SONG).onSuccess { searchResult ->
        val songs = searchResult.items.filterIsInstance<SongItem>()

        val bestMatch =
          songs.firstOrNull { s ->
            s.title.equals(video.title, ignoreCase = true) &&
              s.artists.any { a -> artistMatches(a.name, video.artist) }
          }
            ?: songs.firstOrNull { s ->
              s.title.contains(video.title, ignoreCase = true) &&
                s.artists.any { a -> artistMatches(a.name, video.artist) }
            }
            ?: songs.firstOrNull { s -> s.artists.any { a -> artistMatches(a.name, video.artist) } }
            ?: songs.firstOrNull()

        if (bestMatch != null) {
          withContext(Dispatchers.Main) {
            playerConnection?.playQueue(YouTubeQueue(WatchEndpoint(videoId = bestMatch.id)))
          }
        }
      }
    }
  }
}
