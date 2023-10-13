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

sealed class TestResult{
    data class Success(val formula: Formula, val result: Result): TestResult()
    data class Failure(val formula: Formula, val expect: Result, val actual: Result): TestResult()
    data class Error(val formula: Formula, val error: Throwable): TestResult()

    sealed class Result(): TestResult(){
        data class Number(val value: Int): Result()
        object InvalidFormula: Result()
    }
}

sealed class CalcResult{
    data class Success(val formula: Formula, val result: Int): CalcResult()
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
                StringReader(output.toString()).readLines().last().toIntOrNull()?.let {
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
            ExpressionBuilder(formula.toString()).build().evaluate().toInt()
        }.fold(
            onSuccess = {
                CalcResult.Success(formula,it)
            },
            onFailure = {
                if(it is IllegalArgumentException){
                    CalcResult.Failure(formula, it.message ?: "unknown error")
                }else{
                    TestResult.Error(formula,it)
                }
            }
        )
        val actual = calcBySubject(formula)
        if(expect is CalcResult.Success && actual is CalcResult.Success){
            if(expect.result == actual.result){
                return TestResult.Success(formula,TestResult.Result.Number(expect.result))
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