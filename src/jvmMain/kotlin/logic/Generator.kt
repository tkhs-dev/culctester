package logic

class Generator(
    val operator: Set<Formula.Operator> = setOf(),
    val depth:Int = 1,
) {

}

class Formula(){
    sealed class Operator{
        object Add: Operator()
        object Sub: Operator()
        object Mul: Operator()
        object Div: Operator()
    }
    sealed class Operand{
        data class Number(val value: Int): Operand()
        data class Formula(val formula: Formula?): Operand()
    }
}