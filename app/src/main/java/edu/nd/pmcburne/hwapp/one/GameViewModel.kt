package edu.nd.pmcburne.hwapp.one

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hwapp.one.data.GameRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class GameViewModel(
    private val repository: GameRepository
) : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    data class Query(
        val date: LocalDate,
        val gender: String
    )
    private val queryState = MutableStateFlow(
        Query(
            date = LocalDate.now(),
            gender = "men"
        )
    )

    val gender: StateFlow<String> =
        queryState.map { it.gender }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                "men"
            )
    val selectedDate: StateFlow<LocalDate> =
        queryState.map { it.date }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                LocalDate.now()
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val games = queryState
        .flatMapLatest { query ->
            repository.observeGames(query.date.toString(), query.gender)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            try {
                repository.refreshGames(queryState.value.date, queryState.value.gender)
            } catch (_: Exception) {
                _uiEvents.send(
                    UiEvent.Error(
                        "Refresh failed. Showing last saved scores."
                    )
                )
            }
            isLoading = false
        }
    }
    sealed class UiEvent {
        data class Error(val message: String) : UiEvent()
    }

    fun toggleGender() {
        val newGender = if (queryState.value.gender == "men") "women" else "men"
        queryState.value = queryState.value.copy(gender = newGender)
        refresh()
    }

    fun setDate(date: LocalDate) {
        queryState.value = queryState.value.copy(date = date)
        refresh()
    }
}
