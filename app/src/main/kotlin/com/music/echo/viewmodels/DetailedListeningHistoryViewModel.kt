package echo.music.iad1tya.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import echo.music.iad1tya.db.MusicDatabase
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DetailedListeningHistoryViewModel
@Inject
constructor(
  savedStateHandle: SavedStateHandle,
  val database: MusicDatabase,
) : ViewModel() {
  val startTimestamp: Long =
    savedStateHandle.get<String>("startTimestamp")?.toLongOrNull() ?: 0L

  private val toTimestamp: Long = startTimestamp + 86400000L

  val mostPlayedSongs =
    database
      .mostPlayedSongsStats(startTimestamp, limit = -1, toTimeStamp = toTimestamp)
      .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  val mostPlayedArtists =
    database
      .mostPlayedArtists(startTimestamp, limit = -1, toTimeStamp = toTimestamp)
      .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  val mostPlayedAlbums =
    database
      .mostPlayedAlbums(startTimestamp, limit = -1, toTimeStamp = toTimestamp)
      .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  val totalPlayTime =
    database
      .getTotalPlayTimeInRange(startTimestamp, toTimestamp)
      .map { it ?: 0L }
      .stateIn(viewModelScope, SharingStarted.Lazily, 0L)

  val uniqueSongsCount =
    database
      .getUniqueSongCountInRange(startTimestamp, toTimestamp)
      .stateIn(viewModelScope, SharingStarted.Lazily, 0)

  val uniqueArtistsCount =
    database
      .getUniqueArtistCountInRange(startTimestamp, toTimestamp)
      .stateIn(viewModelScope, SharingStarted.Lazily, 0)

  val uniqueAlbumsCount =
    database
      .getUniqueAlbumCountInRange(startTimestamp, toTimestamp)
      .stateIn(viewModelScope, SharingStarted.Lazily, 0)
}
