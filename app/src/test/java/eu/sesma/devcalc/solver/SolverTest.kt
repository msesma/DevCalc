package eu.sesma.devcalc.solver

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SolverTest {

    private lateinit var solver: Solver

    @Before
    fun setup() {
        solver = Solver()
    }

    @Test
    fun `getOperands extract operands`() {

        val result = solver.getOperands("2+2.225−3×5.0÷6.23−346464", radians = true)

        assertEquals(listOf(2.0, 2.225, 3.0, 5.0, 6.23, 346464.0), result)
    }

    @Test
    fun `getOperators extract operators`() {

        val result = solver.getOperators("2+2.225−3×5.0÷6.23−3464646465642")

        assertEquals(listOf("+", "−", "×", "÷", "−"), result)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `process multiplication`() {
        val operationText = "2+3×5÷6−2×3+9"
        val resultText = "2+15÷6−6+9"
        val calculation = Calculation(
            operands = solver.getOperands(operationText, radians = true) as List<Double>,
            operators = solver.getOperators(operationText)
        )
        val expectedResult = Calculation(
            operands = solver.getOperands(resultText, radians = true) as List<Double>,
            operators = solver.getOperators(resultText)
        )

        val result = solver.processOperand(calculation, "×")

        assertEquals(expectedResult, result)
    }

    @Test
    fun `solve operation`() {
        val operationText = "2+3×4÷6−2×3+9"
        val expectedResult = 7.0

        val result = solver.solve(operationText)

        assertEquals(expectedResult, (result as CalculationResult.Success).result, 0.0)
    }

    @Test
    fun `solve operation with simple brackets`() {
        val operationText = "2+3×(4÷6)−2×(3+9)"
        val expectedResult = -20.0

        val result = solver.solve(operationText)

        assertEquals(expectedResult, (result as CalculationResult.Success).result, 0.0)
    }

    @Test
    fun `solve operation with nested brackets`() {
        val operationText = "2+3×((4÷6)−2×(3+9))"
        val expectedResult = -68.0

        val result = solver.solve(operationText)

        assertEquals(expectedResult, (result as CalculationResult.Success).result, 0.1)
    }

    @Test
    fun `solve operation with no operand syntax error`() {
        val operationText = "2+3×4÷−2×3+9"
        val expectedResult = 6

        val result = solver.solve(operationText)

        assertEquals(expectedResult, (result as CalculationResult.SyntaxError).cursorPosition)
    }

    @Test
    fun `solve operation with two dots syntax error`() {
        val operationText = "2+3×4÷6..4−2×3+9"
        val expectedResult = 10

        val result = solver.solve(operationText)

        assertEquals(expectedResult, (result as CalculationResult.SyntaxError).cursorPosition)
    }

    @Test
    fun `solve malformed brackets syntax error`() {
        val operationText = "2+)6(-5"
        val expectedResult = 2

        val result = solver.solve(operationText)

        assertEquals(expectedResult, (result as CalculationResult.SyntaxError).cursorPosition)
    }

    @Test
    fun `solve malformed brackets syntax error 2`() {
        val operationText = "5-(2+)6(-5"
        val expectedResult = 5

        val result = solver.solve(operationText)

        assertEquals(expectedResult, (result as CalculationResult.SyntaxError).cursorPosition)
    }

    @Test
    fun `fix non existing integer zeroes and ending decimal zeroes or dot`() {
        val operationText = ".2+3×4.÷6.400−2×.3−(.5+1).3+9."
        val expectedResult = "0.2+3×4÷6.4−2×0.3−(0.5+1)0.3+9"

        val result = solver.fixDecimals(operationText)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `fix unmatched brackets`() {
        val operationText = "2−(3+5"
        val expectedResult = "2−(3+5)"

        val result = solver.fixBrackets(operationText)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `fix unmatched brackets 2`() {
        val operationText = "(2−(3+5÷6"
        val expectedResult = "(2−(3+5÷6))"

        val result = solver.fixBrackets(operationText)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `fix unmatched brackets 3`() {
        val operationText = "2−(3+5)−6)"
        val expectedResult = "(2−(3+5)−6)"

        val result = solver.fixBrackets(operationText)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `fix unmatched brackets 4`() {
        val operationText = ")2−)3+(5÷6)"
        val expectedResult = "(()2−)3+(5÷6)"

        val result = solver.fixBrackets(operationText)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `fix missing products`() {
        val operationText = "2(3−1)3+3(5÷6)4)"
        val expectedResult = "2×(3−1)×3+3×(5÷6)×4)"

        val result = solver.fixMissingProducts(operationText)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `resolve square`() {
        val operandText = "2^2"
        val expectedResult = 4.0

        val result = solver.resolvePowers(operandText)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `resolve inverse`() {
        val operandText = "2^-1"
        val expectedResult = 0.5

        val result = solver.resolvePowers(operandText)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `resolve multi pow`() {
        val operandText = "2^2^3"
        val expectedResult = 64.0

        val result = solver.resolvePowers(operandText)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `resolve log`() {
        val operandText = "2+LOG(10)+2"
        val expectedResult = 5.0

        val result = solver.solve(operandText)

        assertEquals(expectedResult, (result as CalculationResult.Success).result, 0.0)
    }

    @Test
    fun `resolve ln`() {
        val operandText = "2+LN(2.7172)+2"
        val expectedResult = 5.0

        val result = solver.solve(operandText)

        assertEquals(expectedResult, (result as CalculationResult.Success).result, 0.1)
    }
}