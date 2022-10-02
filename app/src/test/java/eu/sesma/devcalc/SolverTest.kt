package eu.sesma.devcalc

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class SolverTest {

    lateinit var solver: Solver

    @Before
    fun setup() {
        solver = Solver()
    }

    @Test
    fun `getOperands extract operands`() {

        val result = solver.getOperands("2+2.225−3×5.0÷6.23−346464")

        //Convert to string as comparing array list fails
        assertEquals(
            arrayListOf(2.0, 2.225, 3.0, 5.0, 6.23, 346464.0).toString(),
            result.toString()
        )
    }

    @Test
    fun `getOperators extract operators`() {

        val result = solver.getOperators("2+2.225−3×5.0÷6.23−3464646465642")

        assertEquals(listOf('+', '−', '×', '÷', '−'), result)
    }
}