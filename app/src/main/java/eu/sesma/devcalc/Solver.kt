package eu.sesma.devcalc

import eu.sesma.devcalc.Constants.ADD
import eu.sesma.devcalc.Constants.DIV
import eu.sesma.devcalc.Constants.MUL
import eu.sesma.devcalc.Constants.OPERANDS
import eu.sesma.devcalc.Constants.SUB

class Solver {
    fun solve(noCursorOperation: String): String {
        val operandStrings = noCursorOperation.split(ADD, SUB, MUL, DIV)

        val operators = (0 until operandStrings.size - 1).map { index ->
            noCursorOperation.indexOfAny(
                chars = OPERANDS.toCharArray(),
                startIndex = (0 until index).fold(
                    initial = 0,
                    operation = { acc, i -> acc + operandStrings[i].length }) + index
            )
        }.map { noCursorOperation[it] }
        val operands = operandStrings.map { it.toFloat() }

        return noCursorOperation
    }
}