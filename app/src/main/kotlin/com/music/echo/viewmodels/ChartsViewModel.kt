package echo.music.iad1tya.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import com.music.innertube.pages.ChartsPage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ChartsViewModel @Inject constructor() : ViewModel() {
  private val _chartsPage = MutableStateFlow<ChartsPage?>(null)
  val chartsPage = _chartsPage.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading = _isLoading.asStateFlow()

  private val _error = MutableStateFlow<String?>(null)
  val error = _error.asStateFlow()

  fun loadCharts() {
    viewModelScope.launch {
      _isLoading.value = true
      _error.value = null

      val result = YouTube.getChartsPage()
      val page = result.getOrNull()
      if (result.isSuccess && page != null && page.sections.isNotEmpty()) {
        _chartsPage.value = page
      } else {
        // Fallback 1: explore new releases
        val exploreResult = YouTube.explore()
        val explore = exploreResult.getOrNull()
        if (explore != null && !explore.newReleaseAlbums.isNullOrEmpty()) {
          val fallbackItems = explore.newReleaseAlbums.map { album ->
            SongItem(
              id = album.id,
              title = album.title,
              artists = album.artists ?: emptyList(),
              album = null,
              duration = null,
              thumbnail = album.thumbnail,
              explicit = false,
              endpoint = null
            )
          }
          _chartsPage.value = ChartsPage(
            sections = listOf(
              ChartsPage.ChartSection(
                title = "Top Trending Hits",
                items = fallbackItems,
                chartType = ChartsPage.ChartType.TRENDING
              )
            ),
            continuation = null
          )
        } else {
          // Fallback 2: search top hits
          YouTube.searchSummary("Top 50 Hits").onSuccess { summary ->
            val songs = summary.summaries.flatMap { it.items }.filterIsInstance<SongItem>()
            if (songs.isNotEmpty()) {
              _chartsPage.value = ChartsPage(
                sections = listOf(
                  ChartsPage.ChartSection(
                    title = "Top Trending Hits",
                    items = songs,
                    chartType = ChartsPage.ChartType.TRENDING
                  )
                ),
                continuation = null
              )
            }
          }
        }
      }

      _isLoading.value = false
    }
  }

  fun loadMore() {
    viewModelScope.launch {
      _chartsPage.value?.continuation?.let { continuation ->
        _isLoading.value = true
        YouTube.getChartsPage(continuation)
          .onSuccess { newPage ->
            _chartsPage.value =
              _chartsPage.value?.copy(
                sections = _chartsPage.value?.sections.orEmpty() + newPage.sections,
                continuation = newPage.continuation
              )
          }
          .onFailure { e -> _error.value = "Failed to load more: ${e.message}" }
        _isLoading.value = false
      }
    }
  }
}
