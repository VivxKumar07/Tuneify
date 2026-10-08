package com.music.echo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import echo.music.iad1tya.db.MusicDatabase
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DayUsageData(
  val dayName: String,
  val totalMs: Long,
  val songsMs: Long,
  val artistsMs: Long,
  val albumsMs: Long,
  val timestamp: Long = 0L,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ListeningSummaryViewModel
@Inject
constructor(
  val database: MusicDatabase,
) : ViewModel() {
  val weekOffset = MutableStateFlow(0)

  val weekDailyData: StateFlow<List<DayUsageData>> =
    weekOffset
      .flatMapLatest { offset ->
        val now = LocalDate.now().minusWeeks(offset.toLong())
        val monday = now.with(DayOfWeek.MONDAY)
        val dayFlows =
          (0..6).map { dayIndex ->
            val day = monday.plusDays(dayIndex.toLong())
            val start = day.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            val end = day.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            val dayName = day.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
            database.getTotalPlayTimeInRange(start, end).map { playTime ->
              val ms = playTime ?: 0L
              DayUsageData(
                dayName = dayName,
                totalMs = ms,
                songsMs = ms,
                artistsMs = ms,
                albumsMs = ms,
                timestamp = start,
              )
            }
          }
        combine(dayFlows) { it.toList() }
      }
      .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  val weekTotalMs: StateFlow<Long> =
    weekDailyData
      .map { list -> list.sumOf { it.totalMs } }
      .stateIn(viewModelScope, SharingStarted.Lazily, 0L)

  fun goToPreviousWeek() {
    weekOffset.value++
  }

  fun goToNextWeek() {
    if (weekOffset.value > 0) {
      weekOffset.value--
    }
  }

  fun isCurrentWeek(): Boolean = weekOffset.value == 0

  fun setWeekFromEpoch(epochMillis: Long) {
    val targetDate = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    val today = LocalDate.now()
    val diffDays = ChronoUnit.DAYS.between(targetDate, today)
    weekOffset.value = (diffDays / 7).toInt().coerceAtLeast(0)
  }
}
