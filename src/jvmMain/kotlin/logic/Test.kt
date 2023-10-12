package logic

import net.objecthunter.exp4j.ExpressionBuilder
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.ExecuteWatchdog
import org.apache.commons.exec.PumpStreamHandler
import java.io.File
import java.io.InputStreamReader
import java.io.StringReader
import java.io.StringWriter

data class TestResult(val formula: Formula, val expect: Int, val actual: Int){
    val isSuccess = expect == actual
}

class Tester(){
    val files = File("test").also { if(!it.exists()) it.mkdir() }
        .listFiles { it -> it?.name?.endsWith(".exe") ?: false }

    val cmd = CommandLine(files.first().absolutePath)
    val executor = DefaultExecutor()
    val watchdog = ExecuteWatchdog(60000)
    executor.watchdog = watchdog

    init{

    }

    private fun calcBySubject(formula: Formula): Int{
        val input = InputStreamReader(Stri)
        runCatching {
            val hndlr = PumpStreamHandler(System.out,System.err,System.`in`)
            executor.streamHandler = hndlr
            executor.setExitValue(0)
            executor.execute(cmd)
        }
    }

    fun test(formula: Formula): TestResult{
        val expect = ExpressionBuilder(formula.toString()).build().evaluate().toInt()
        val actual = 1
        return TestResult(formula, expect, actual)
    }
}