package logic

class Formula(private val operand1: Operand, private val operand2: Operand?, private val operator: Operator?, var isNested:Boolean = false) {
    interface Element
    sealed class Operator:Element{
        object Add: Operator()
        object Sub: Operator()
        object Mul: Operator()
        object Div: Operator()
        object Pow: Operator()
    }
    sealed class Operand:Element{
        data class Number(val value: Double): Operand(){
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
                        Operator.Pow -> "^"
                    })
                append(operand2)
            }
            if (isNested) append(")")
            toString()
        }
    }
}