package logic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class Generator(
    val operator: Set<Formula.Operator> = setOf(Formula.Operator.Add, Formula.Operator.Sub, Formula.Operator.Mul),
) {
    suspend fun generate(depth:Int = 0,maxNum: Int): Formula {
        return _generate(depth,maxNum, depth > 0 && Math.random() > 0.8)
    }
    private suspend fun _generate(depth: Int, maxNum: Int,isNested: Boolean): Formula {
        return withContext(Dispatchers.Default){
            Formula(
                generateOperand(depth,maxNum),
                generateOperand(depth,maxNum),
                generateOperator(),
                isNested
            )
        }

    }

    private fun generateOperator(): Formula.Operator? {
        return operator.randomOrNull()
    }

    private suspend fun generateOperand(depth: Int, maxNum:Int): Formula.Operand {
        return if (depth > 0) {
            if(Math.random() > 0.5){
                Formula.Operand.Formula(
                    _generate(depth - 1, maxNum, true)
                )
            }else{
                Formula.Operand.Formula(
                    _generate(0, maxNum,false)
                )
            }

        } else {
            if(Math.random() > 0.8){
                Formula.Operand.Formula(
                    _generate(0, maxNum,false)
                )
            }else{
                Formula.Operand.Number(Random.nextInt(maxNum*2)-maxNum)
            }
        }
    }
}

class Formula(private val operand1: Operand, private val operand2: Operand?, private val operator: Operator?, var isNested:Boolean = false) {
    interface Element
    sealed class Operator:Element{
        object Add: Operator()
        object Sub: Operator()
        object Mul: Operator()
        object Div: Operator()
    }
    sealed class Operand:Element{
        data class Number(val value: Int): Operand(){
            override fun toString(): String {
                if(value < 0)
                    return "($value)"
                else
                    return value.toString()
            }
        }
        data class Formula(val formula: logic.Formula): Operand(){
            override fun toString(): String {
                return formula.toString()
            }
        }
    }

    override fun toString(): String {
        return with(StringBuilder()){
            if (isNested) append("(")
            append(operand1)
            if (operator != null && operand2 != null){
                append(
                    when(operator){
                        Operator.Add -> "+"
                        Operator.Div -> "/"
                        Operator.Mul -> "*"
                        Operator.Sub -> "-"
                    })
                append(operand2)
            }
            if (isNested) append(")")
            toString()
        }
    }
}