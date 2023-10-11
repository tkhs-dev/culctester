package ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainScreenModel() {
    data class UiState(
        val containSub: Boolean = false,
        val containMul: Boolean = true,
        val containMoreDigits: Boolean = false,
        val containNegative: Boolean = false,
        val depth: Int = 0,
        val maxLength: Int = 200,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onUiStateChanged(uiState: UiState) {
        _uiState.value = uiState
    }
}