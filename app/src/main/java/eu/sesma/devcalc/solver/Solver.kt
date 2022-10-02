package eu.sesma.devcalc.solver

import androidx.annotation.VisibleForTesting
import eu.sesma.devcalc.editor.Constants.ADD
import eu.sesma.devcalc.editor.Constants.DIV
import eu.sesma.devcalc.editor.Constants.MUL
import eu.sesma.devcalc.editor.Constants.OPERANDS
import eu.sesma.devcalc.editor.Constants.SUB

class Solver {

    fun solve(operationText: String): String {
        var calculation = Calculation(
            operands = getOperands(operationText),
            operators = getOperators(operationText)
        )

        listOf(MUL, DIV, SUB, ADD).forEach { currentOperand ->
            calculation = processOperand(calculation, currentOperand)
        }

        return calculation.operands[0].toString()
    }

    @VisibleForTesting()
    internal fun getOperands(operationText: String): List<Double> {
        val operandStrings = operationText.split(ADD, SUB, MUL, DIV)
        return operandStrings.map { it.toDouble() }
    }

    @VisibleForTesting
    internal fun getOperators(operationText: String): List<String> {
        val operandStrings = operationText.split(ADD, SUB, MUL, DIV)
        return (0 until operandStrings.size - 1).map { index ->
            operationText.indexOfAny(
                chars = OPERANDS.toCharArray(),
                startIndex = (0 until index).fold(
                    initial = 0,
                    operation = { acc, i -> acc + operandStrings[i].length }) + index
            )
        }.map { operationText[it].toString() }
    }

    @VisibleForTesting
    internal fun processOperand(calculation: Calculation, operator: String): Calculation {
        var tempCalculation = calculation
        var operatorPosition = tempCalculation.operators.indexOfFirst { it == operator }
        while (operatorPosition != -1) {
            val operands = tempCalculation.operands[operatorPosition] to tempCalculation.operands[operatorPosition + 1]
            val result = when (operator) {
                ADD -> operands.first + operands.second
                SUB -> operands.first - operands.second
                MUL -> operands.first * operands.second
                DIV -> operands.first / operands.second
                else -> 0.0 // Cannot happen
            }
            val operandList = tempCalculation.operands.toMutableList().apply {
                this[operatorPosition] = result
                removeAt(operatorPosition + 1)
            }
            val operatorList = tempCalculation.operators.toMutableList().apply { removeAt(operatorPosition) }
            tempCalculation = Calculation(operandList, operatorList)
            operatorPosition = tempCalculation.operators.indexOfFirst { it == operator }
        }
        return tempCalculation
    }
}