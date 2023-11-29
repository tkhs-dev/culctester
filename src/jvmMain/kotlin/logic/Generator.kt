package logic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class Generator(
    val operator: Set<Formula.Operator> = setOf(Formula.Operator.Add, Formula.Operator.Sub, Formula.Operator.Mul),
) {
    suspend fun generate(depth:Int = 0, maxNum: Int = 10, allowNegative: Boolean = true, allowReal: Boolean, allowOuterBracket: Boolean = false): Formula {
        return _generate(depth,maxNum, allowNegative, allowReal, allowOuterBracket && depth > 0 && Math.random() > 0.8)
    }
    private suspend fun _generate(depth: Int, maxNum: Int, allowNegative: Boolean, allowReal: Boolean, isNested: Boolean): Formula {
        return withContext(Dispatchers.Default){
            val op = generateOperator()
            Formula(
                generateOperand(depth,maxNum,allowNegative,allowReal),
                if(op is Formula.Operator.Pow)
                    generateOperand(0,3,false,false)
                else
                    generateOperand(depth,maxNum,allowNegative,allowReal),
                op,
                isNested
            )
        }

    }

    private fun generateOperator(): Formula.Operator? {
        return operator.randomOrNull()
    }

    private suspend fun generateOperand(depth: Int, maxNum:Int, allowNegative: Boolean, allowReal: Boolean): Formula.Operand {
        return if (depth > 0) {
            if(Math.random() > 0.5){
                Formula.Operand.Formula(
                    _generate(depth - 1, maxNum, allowNegative, allowReal, true)
                )
            }else{
                Formula.Operand.Formula(
                    _generate(0, maxNum, allowNegative, allowReal, false)
                )
            }

        } else {
            if(Math.random() > 0.8){
                Formula.Operand.Formula(
                    _generate(0, maxNum, allowNegative, allowReal, false)
                )
            }else{
                Formula.Operand.Number(
                    if(allowReal && Math.random() > 0.5)
                        Math.ceil(Random.nextDouble(maxNum.toDouble())*100)/100 * (if(allowNegative && Math.random() > 0.5) -1 else 1)
                    else
                        (Random.nextInt(maxNum) * if(allowNegative && Math.random() > 0.5) -1 else 1).toDouble()
                )
            }
        }
    }
}