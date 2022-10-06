package eu.sesma.devcalc.solver

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class SolverTest {

    private lateinit var solver: Solver

    @Before
    fun setup() {
        solver = Solver()
    }

    @Test
    fun `getOperands extract operands`() {

        val result = solver.getOperands("2+2.225−3×5.0÷6.23−346464")

        assertEquals(
            listOf(2.0, 2.225, 3.0, 5.0, 6.23, 346464.0),
            result
        )
    }

    @Test
    fun `getOperators extract operators`() {

        val result = solver.getOperators("2+2.225−3×5.0÷6.23−3464646465642")

        assertEquals(listOf("+", "−", "×", "÷", "−"), result)
    }

    @Test
    fun `process multiplication`() {
        val operationText = "2+3×5÷6−2×3+9"
        val resultText = "2+15÷6−6+9"
        val calculation = Calculation(
            operands = solver.getOperands(operationText),
            operators = solver.getOperators(operationText)
        )
        val expectedResult = Calculation(
            operands = solver.getOperands(resultText),
            operators = solver.getOperators(resultText)
        )

        val result = solver.processOperand(calculation, "×")

        assertEquals (expectedResult, result)
    }

    @Test
    fun `solve operation`() {
        val operationText = "2+3×4÷6−2×3+9"
        val expectedResult = 7.0

        val result = solver.solve(operationText)

        assertEquals (expectedResult, (result as CalculationResult.Success).result, 0.0)
    }
}