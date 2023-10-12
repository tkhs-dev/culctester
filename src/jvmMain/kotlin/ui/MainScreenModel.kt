package ui

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logic.Formula
import logic.Generator
import java.awt.image.ByteLookupTable

class MainScreenModel() {
    data class UiState(
        val containSub: Boolean = false,
        val containMul: Boolean = true,
        val containMoreDigits: Boolean = false,
        val containNegative: Boolean = false,
        val depth: Int = 0,
        val maxLength: Int = 200,

        val tab: Tab = Tab.ERROR,
    ){
        enum class Tab{
            SUCCESS,
            ERROR,
        }
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    val formula = MutableStateFlow<Formula?>(null)

    fun generateFormula(): Flow<Formula>{
        return flow {
            repeat(10000){
                emit(Generator().generate(1,100))
            }
        }
    }

    fun onUiStateChanged(uiState: UiState) {
        _uiState.value = uiState
    }

    suspend fun onExecuteClicked() {
        println("generate clicked")
        generateFormula().collect {
            formula.emit(it)
        }
    }

    suspend fun onRetryClicked() {
        println("retry clicked")
    }
}