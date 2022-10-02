package eu.sesma.devcalc

import androidx.annotation.VisibleForTesting
import eu.sesma.devcalc.Constants.ADD
import eu.sesma.devcalc.Constants.DIV
import eu.sesma.devcalc.Constants.MUL
import eu.sesma.devcalc.Constants.OPERANDS
import eu.sesma.devcalc.Constants.SUB

class Solver {

    fun solve(operation: String): String {
        val operands = getOperands(operation)
        val operators = getOperators(operation)

        return operation
    }

    @VisibleForTesting()
    internal fun getOperands(operation: String):List<Float> {
        val operandStrings = operation.split(ADD, SUB, MUL, DIV)
        return operandStrings.map { it.toFloat() }
    }

    @VisibleForTesting
    internal fun getOperators(operation: String): List<Char> {
        val operandStrings = operation.split(ADD, SUB, MUL, DIV)
        return (0 until operandStrings.size - 1).map { index ->
            operation.indexOfAny(
                chars = OPERANDS.toCharArray(),
                startIndex = (0 until index).fold(
                    initial = 0,
                    operation = { acc, i -> acc + operandStrings[i].length }) + index
            )
        }.map { operation[it] }
    }
}