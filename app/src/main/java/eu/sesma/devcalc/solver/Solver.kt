package eu.sesma.devcalc.solver

import androidx.annotation.VisibleForTesting
import eu.sesma.devcalc.editor.Constants.ADD
import eu.sesma.devcalc.editor.Constants.DIV
import eu.sesma.devcalc.editor.Constants.LBRKT
import eu.sesma.devcalc.editor.Constants.MUL
import eu.sesma.devcalc.editor.Constants.OPERANDS
import eu.sesma.devcalc.editor.Constants.RBRKT
import eu.sesma.devcalc.editor.Constants.SUB
import eu.sesma.devcalc.solver.CalculationResult.Success
import eu.sesma.devcalc.solver.CalculationResult.SyntaxError

class Solver {

    fun solve(operationText: String): CalculationResult {
        val operands = getOperands(operationText)
        val errorIndex = operands.indexOf(null)
        if (errorIndex != -1) return SyntaxError(cursorPosition = getSyntaxErrorPosition(operationText, errorIndex))

        @Suppress("UNCHECKED_CAST")
        var calculation = Calculation(
            operands = operands as List<Double>,
            operators = getOperators(operationText)
        )

        listOf(MUL, DIV, SUB, ADD).forEach { currentOperand ->
            calculation = processOperand(calculation, currentOperand)
        }

        return Success(result = calculation.operands[0])
    }

    fun fixSyntax(operationText: String): String {
        var tmpOperation = fixDecimals(operationText)
        tmpOperation = fixBrackets(tmpOperation)
        return fixMissingProducts(tmpOperation)
    }

    @VisibleForTesting
    internal fun getOperands(operationText: String): List<Double?> {
        val operandStrings = operationText.split(ADD, SUB, MUL, DIV)
        return operandStrings.map {
            try {
                it.toDouble()
            } catch (nfe: java.lang.NumberFormatException) {
                null
            }
        }
    }

    @VisibleForTesting
    internal fun getOperators(operationText: String): List<String> {
        val operandStrings = operationText.split(ADD, SUB, MUL, DIV)
        return extractOperatorList(
            operandStrings = operandStrings,
            operationText = operationText,
            operands = OPERANDS.toCharArray()
        )
    }

    @VisibleForTesting
    internal fun processOperand(calculation: Calculation, operator: String): Calculation {
        var tempCalculation = calculation
        var operatorPosition = tempCalculation.operators.indexOfFirst { it == operator }
        while (operatorPosition != -1) {
            val operands =
                tempCalculation.operands[operatorPosition] to tempCalculation.operands[operatorPosition + 1]
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

    @VisibleForTesting
    internal fun fixDecimals(operationText: String): String {
        val operandStrings = operationText.split(ADD, SUB, MUL, DIV, LBRKT, RBRKT)
        val fixedOperandStrings = operandStrings.map { operand ->
            if (operand.isNotEmpty() && operand[0] == '.') "0$operand" else operand
        }.map { operand ->
            if (operand.contains(".")) removeEndingZeroes(operand) else operand
        }.map { operand ->
            operand.removeSuffix(".")
        }

        val operators = extractOperatorList(
            operandStrings = operandStrings,
            operationText = operationText,
            operands = (ADD + SUB + MUL + DIV + LBRKT + RBRKT).toCharArray()
        )

        return fixedOperandStrings
            .zip(operators, transform = { a, b -> a + b })
            .joinToString(separator = "") + fixedOperandStrings.last()
    }

    @VisibleForTesting
    internal fun fixBrackets(operationText: String): String {
        val diff = operationText.count { it.toString() == LBRKT } - operationText.count { it.toString() == RBRKT }
        return when {
            diff == 0 -> operationText
            diff < 0 -> operationText.padStart(operationText.length - diff, LBRKT[0])
            else -> operationText.padEnd(operationText.length + diff, RBRKT[0])
        }
    }

    @VisibleForTesting
    internal fun fixMissingProducts(operationText: String): String {
        val operandStrings = operationText.split(LBRKT, RBRKT)
        val operators = extractOperatorList(
            operandStrings = operandStrings,
            operationText = operationText,
            operands = (LBRKT + RBRKT).toCharArray()
        )
        val fixedOperators = operators.mapIndexed { index, operator ->
            val lastOperand = operandStrings[index]
            val nextOperand = operandStrings[index + 1]
            when {
                lastOperand.isNotEmpty() && lastOperand.last().isDigit() && operators[index] == LBRKT -> MUL + LBRKT
                nextOperand.isNotEmpty() && nextOperand.first().isDigit() && operators[index] == RBRKT -> RBRKT + MUL
                else -> operator
            }
        }

        return operandStrings
            .zip(fixedOperators, transform = { a, b -> a + b })
            .joinToString(separator = "") + operandStrings.last()
    }

    private fun removeEndingZeroes(operand: String): String {
        var cleanOperand = operand
        while (cleanOperand.last() == '0') {
            cleanOperand = cleanOperand.removeSuffix("0")
        }
        return cleanOperand
    }

    private fun getSyntaxErrorPosition(operationText: String, errorIndex: Int): Int {
        val operandStrings = operationText.split(ADD, SUB, MUL, DIV)
        return (0 until errorIndex).fold(
            initial = 0,
            operation = { acc, i -> acc + operandStrings[i].length }) + errorIndex + operandStrings[errorIndex].length
    }

    private fun extractOperatorList(
        operandStrings: List<String>,
        operationText: String,
        operands: CharArray
    ) = (0 until operandStrings.size - 1).map { index ->
        operationText.indexOfAny(
            chars = operands,
            startIndex = (0 until index).fold(
                initial = 0,
                operation = { acc, i -> acc + operandStrings[i].length }) + index
        )
    }.map { operationText[it].toString() }
}