package logic

import net.objecthunter.exp4j.ExpressionBuilder
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.ExecuteWatchdog
import org.apache.commons.exec.PumpStreamHandler
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.StringReader
import java.lang.NumberFormatException
import java.math.BigDecimal

sealed class TestResult(val formula: Formula){
    data class Success(private val _formula: Formula, val result: Result): TestResult(_formula)
    data class Failure(private val _formula: Formula, val expect: Result, val actual: Result): TestResult(_formula)
    data class Error(private val _formula: Formula, val error: Throwable): TestResult(_formula)

    sealed class Result(){
        data class Number(val value: Double): Result(){
            override fun toString(): String {
                return value.toString()
            }
        }
        object InvalidFormula: Result(){
            override fun toString(): String {
                return "InvalidFormula"
            }
        }
        object Overflow: Result(){
            override fun toString(): String {
                return "Overflow"
            }
        }
        data class Error(val error: String): Result(){
            override fun toString(): String {
                return error
            }
        }
    }
}

sealed class CalcResult{
    data class Success(val formula: Formula, val result: Double): CalcResult()
    data class Failure(val formula: Formula, val result: String): CalcResult()
    data class Error(val formula: Formula, val error: Throwable): CalcResult()
}


class Tester(){
    private val filePath:String

    init{
        val files = File("test").also { if(!it.exists()) it.mkdir() }
            .listFiles { it -> it?.name?.endsWith(".exe") ?: false }
        filePath = files.first().absolutePath
    }

    private fun calcBySubject(formula: Formula): CalcResult{
        val cmd = CommandLine(filePath)
        val executor = DefaultExecutor()
        val watchdog = ExecuteWatchdog(60000)
        executor.watchdog = watchdog
        val input = ByteArrayInputStream(formula.toString().toByteArray())
        val output = ByteArrayOutputStream()
        val error = ByteArrayOutputStream()
        runCatching {
            executor.streamHandler = PumpStreamHandler(output,error,input)
            executor.setExitValues(null)
            if(executor.execute(cmd) >= 0){
                StringReader(output.toString()).readLines().last().toDoubleOrNull()?.let {
                    CalcResult.Success(formula,it)
                } ?: CalcResult.Failure(formula,output.toString())
            }else{
                CalcResult.Failure(formula,output.toString())
            }
        }.onSuccess {
            return it
        }.onFailure {
            return CalcResult.Error(formula,it)
        }
        input.close()
        output.close()
        error.close()
        return CalcResult.Error(formula,Exception("unknown internal error"))
    }

    fun test(formula: Formula): TestResult{
        val expect = runCatching {
            ExpressionBuilder(formula.toString()).build().evaluate()
        }.map {
            runCatching {
                BigDecimal(it).toDouble()
            }.getOrDefault(Double.MAX_VALUE)
        }.fold(
            onSuccess = {
                CalcResult.Success(formula,it)
            },
            onFailure = {
                if(it is IllegalArgumentException || it is ArithmeticException ){
                    CalcResult.Failure(formula, it.message ?: "unknown error")
                }else{
                    CalcResult.Error(formula,it)
                }
            }
        )
        val actual = calcBySubject(formula)
        if(expect is CalcResult.Success && actual is CalcResult.Success){
            if(Math.abs(expect.result - actual.result) <= 1e10){
                return TestResult.Success(formula,TestResult.Result.Number(actual.result))
            }else if(expect.result == Double.MAX_VALUE || expect.result == Double.MIN_VALUE){
                return TestResult.Failure(formula,TestResult.Result.Overflow,TestResult.Result.Overflow)
            }else{
                return TestResult.Failure(formula,TestResult.Result.Number(expect.result),TestResult.Result.Number(actual.result))
            }
        }else if(expect is CalcResult.Failure && actual is CalcResult.Failure){
            return TestResult.Success(formula,TestResult.Result.InvalidFormula)
        }else if (expect is CalcResult.Failure && actual is CalcResult.Success) {
            return TestResult.Failure(
                formula,
                TestResult.Result.InvalidFormula,
                TestResult.Result.Number(actual.result)
            )
        } else if (expect is CalcResult.Success && actual is CalcResult.Failure) {
            return TestResult.Failure(
                formula,
                TestResult.Result.Number(expect.result),
                TestResult.Result.InvalidFormula
            )
        } else if(expect is CalcResult.Error && actual is CalcResult.Error) {
            return TestResult.Error(formula, InternalError("unknown internal error"))
        }else if(expect is CalcResult.Error || actual is CalcResult.Error){
            return TestResult.Error(formula,if (expect is CalcResult.Error) expect.error else (actual as CalcResult.Error).error)
        }else{
            return TestResult.Error(formula,InternalError("unknown internal error"))
        }
    }
}