package ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import logic.Formula
import logic.Generator
import logic.TestResult
import logic.Tester

class MainScreenModel() {
    data class UiState(
        val containSub: Boolean = true,
        val containMul: Boolean = true,
        val containDiv: Boolean = false,
        val containPow: Boolean = false,
        val containMoreDigits: Boolean = false,
        val containNegative: Boolean = false,
        val containReal: Boolean = false,
        val allowOuterBracket: Boolean = false,
        val depth: Int = 0,
        val maxLength: Int = 200,
        val trialCount: Int = 100,

        val isLoading: Boolean = false,
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

    fun onUiStateChanged(uiState: UiState) {
        _uiState.value = uiState
    }

    suspend fun onExecuteClicked() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val op = mutableSetOf<Formula.Operator>()
        op.add(Formula.Operator.Add)
        if(_uiState.value.containSub) op.add(Formula.Operator.Sub)
        if(_uiState.value.containMul) op.add(Formula.Operator.Mul)
        if(_uiState.value.containDiv) op.add(Formula.Operator.Div)
        if(_uiState.value.containPow) op.add(Formula.Operator.Pow)
        val generator = Generator(op)
        val tester = Tester()

        _testResults.value = runBlocking(Dispatchers.Default) {
            (1.._uiState.value.trialCount)
                .map {
                    async {
                        var f = generator.generate(depth = _uiState.value.depth, maxNum = if(_uiState.value.containMoreDigits) 999 else 9, allowNegative = _uiState.value.containNegative, allowReal = _uiState.value.containReal, allowOuterBracket = _uiState.value.allowOuterBracket)
                        var count: Int = 0
                        while(f.toString().length > _uiState.value.maxLength){
                            f = generator.generate(depth = _uiState.value.depth, maxNum = if(_uiState.value.containMoreDigits) 999 else 9, allowNegative = _uiState.value.containNegative, allowReal = _uiState.value.containReal, allowOuterBracket = _uiState.value.allowOuterBracket)
                            count++
                            if(count > 100){
                                f = Formula(Formula.Operand.Number(0.0),null,null)
                                break
                            }
                        }
                        tester.test(f)
                    }
                }.awaitAll()
        }.also { _uiState.value = _uiState.value.copy(isLoading = false) }
    }

    suspend fun onRetryClicked() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        runBlocking(Dispatchers.Default) {
            _testResults.value = _testResults.value.map { it.formula }
                .map {
                    async {
                        Tester().test(it)
                    }
                }.awaitAll()
        }.also { _uiState.value = _uiState.value.copy(isLoading = false) }
    }
}