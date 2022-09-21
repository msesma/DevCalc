package eu.sesma.devcalc

import android.util.Log
import androidx.compose.runtime.mutableStateOf

class Editor {

    enum class Actions {
        ANSWER, ENTER, ESC, DELETE, BACK, FORTH
    }

    companion object {
        const val CURSOR = "|"
    }

    private var currentCalculation = CalculationLine()
    private var calculations = mutableListOf<CalculationLine>()
    private var cursorPosition = 0
    private var fieldSelected: Pair<Int, Int>? = null

    val calculationsState = mutableStateOf(listOf(currentCalculation))

    fun onScreenClicked(lineIndex: Int, fieldIndex: Int) {
        if (lineIndex == 0) {
            currentCalculation = currentCalculation.copy(
                operation = currentCalculation.operation.replaceFirst(CURSOR, "") + CURSOR
            )
            cursorPosition = currentCalculation.operation.length - 1
            calculationsState.value = listOf(currentCalculation) + calculations
            return
        }

        val stackIndex = lineIndex - 1
        fieldSelected?.let {
            if (it.first != stackIndex || it.second != fieldIndex) {
                calculations[it.first] = calculations[it.first].copy(fieldSelected = -1)
            }
        }
        fieldSelected = stackIndex to fieldIndex

        if (calculations[stackIndex].fieldSelected == -1)
            calculations[stackIndex] = calculations[stackIndex].copy(fieldSelected = fieldIndex)
        else {
            val value = if (fieldIndex == 0) calculations[stackIndex].operation else calculations[stackIndex].result
            calculations[stackIndex] = calculations[stackIndex].copy(fieldSelected = -1)
            currentCalculation = currentCalculation.copy(
                operation = currentCalculation.operation.replaceFirst(
                    CURSOR,
                    "$value${CURSOR}"
                )
            )
            fieldSelected = null
        }

        calculationsState.value = listOf(currentCalculation) + calculations
    }

    fun onKeyClicked(keyCode: Int) {
        val keyValue = when (keyCode) {
            0 -> "0"
            1 -> "."
            3 -> "+"
            5 -> "1"
            6 -> "2"
            7 -> "3"
            8 -> "-"
            10 -> "4"
            11 -> "5"
            12 -> "6"
            13 -> "x"
            15 -> "7"
            16 -> "8"
            17 -> "9"
            18 -> "/"
            else -> null
        }
        val keyAction = when (keyCode) {
            2 -> Actions.ANSWER
            4 -> Actions.ENTER
            20 -> Actions.ESC
            21 -> Actions.DELETE
            22 -> Actions.BACK
            23 -> Actions.FORTH
            else -> null
        }

        if (keyValue != null) {
            currentCalculation = currentCalculation.copy(
                operation = currentCalculation.operation.replaceFirst(
                    CURSOR,
                    "$keyValue${CURSOR}"
                )
            )
            calculationsState.value = listOf(currentCalculation) + calculations
            cursorPosition++
        }

        if (keyAction != null) {
            when (keyAction) {
                Actions.ESC -> fieldSelected?.let {
                    calculations[it.first] = calculations[it.first].copy(fieldSelected = -1)
                    fieldSelected = null
                } ?: run {
                    currentCalculation = CalculationLine()
                    cursorPosition = 0
                }
                Actions.DELETE -> {
                    if (cursorPosition == 0) return
                    currentCalculation = currentCalculation.copy(
                        operation = currentCalculation.operation.removeRange(cursorPosition - 1, cursorPosition)
                    )
                    cursorPosition--
                }
                Actions.ENTER -> {
                    val noCursorOperation = currentCalculation.operation.replaceFirst(CURSOR, "")
                    if (noCursorOperation.isEmpty()) return

                    val newResult = currentCalculation.copy(operation = noCursorOperation, result = noCursorOperation)
                    calculations.add(0, newResult)
                    currentCalculation = CalculationLine()
                    cursorPosition = 0
                }
                Actions.ANSWER -> {
                    if (calculations.isEmpty()) return
                    Log.d("==>", "pre ans cursorPosition = $cursorPosition")
                    val value = calculations[0].result
                    currentCalculation = currentCalculation.copy(
                        operation = currentCalculation.operation.replaceFirst(
                            CURSOR,
                            "$value${CURSOR}"
                        )
                    )
                    cursorPosition += value.length
                    Log.d("==>", "post ans cursorPosition = $cursorPosition")
                }
                Actions.BACK -> if (cursorPosition > 0) {
                    cursorPosition--
                    addCursor()
                }
                Actions.FORTH -> if (cursorPosition < currentCalculation.operation.length - 1) {
                    cursorPosition++
                    addCursor()
                }
            }
            calculationsState.value = listOf(currentCalculation) + calculations
        }
    }

    private fun addCursor() {
        val noCursorOperation = currentCalculation.operation.replaceFirst(CURSOR, "")
        currentCalculation = currentCalculation.copy(
            operation = noCursorOperation.substring(0, cursorPosition) +
                    CURSOR +
                    noCursorOperation.substring(cursorPosition, noCursorOperation.length)
        )
    }
}