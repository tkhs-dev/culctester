package ui

import kotlinx.coroutines.flow.*
import logic.Formula
import logic.Generator
import logic.TestResult
import logic.Tester

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

    private val _testResults = MutableStateFlow(listOf<TestResult>())
    val testResult = _testResults.asStateFlow()

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
        val generator = Generator()
        val tester = Tester()
        _testResults.value = (1..100).map { generator.generate(depth = 1, maxNum = 100) }
            .map { tester.test(it) }
            .toList()
    }

    suspend fun onRetryClicked() {
        println("retry clicked")
    }
}