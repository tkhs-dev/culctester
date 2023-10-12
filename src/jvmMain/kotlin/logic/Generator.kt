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