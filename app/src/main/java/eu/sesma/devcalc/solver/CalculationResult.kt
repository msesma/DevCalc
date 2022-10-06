package eu.sesma.devcalc.solver

sealed class CalculationResult {
    class Success(val result: Double) : CalculationResult()
    class SyntaxError(val cursorPosition: Int) : CalculationResult()
    class DivideByZero(val cursorPosition: Int) : CalculationResult()
}
