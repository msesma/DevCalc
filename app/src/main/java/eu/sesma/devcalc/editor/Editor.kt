package eu.sesma.devcalc.editor

import androidx.compose.runtime.mutableStateOf
import eu.sesma.devcalc.editor.Constants.ADD
import eu.sesma.devcalc.editor.Constants.CURSOR
import eu.sesma.devcalc.editor.Constants.DIV
import eu.sesma.devcalc.editor.Constants.MUL
import eu.sesma.devcalc.editor.Constants.SUB
import eu.sesma.devcalc.editor.Constants.SYNTAX_ERROR
import eu.sesma.devcalc.editor.Editor.Actions.*
import eu.sesma.devcalc.solver.CalculationResult.Success
import eu.sesma.devcalc.solver.CalculationResult.SyntaxError
import eu.sesma.devcalc.solver.Solver

class Editor(val solver: Solver) {

    enum class Actions {
        ANSWER, ENTER, ESC, DELETE, BACK, FORTH
    }

    private var currentCalculation = CalculationLine()
    private var calculations = mutableListOf<CalculationLine>()
    private var cursorPosition = 0
    private var fieldSelected: Pair<Int, Int>? = null

    val calculationsState = mutableStateOf(listOf(currentCalculation))
    val errorState = mutableStateOf("")

    fun onKeyClicked(keyCode: Int) {
        errorState.value = ""
        val keyValue = getKeyValue(keyCode)
        val keyAction = getKeyAction(keyCode)

        if (keyValue != null) processKeyValue(keyValue)

        if (keyAction != null) when (keyAction) {
            ESC -> executeActionEsc()
            DELETE -> executeActionDelete()
            ENTER -> executeActionEnter()
            ANSWER -> executeActionAnswer()
            BACK -> executeActionBack()
            FORTH -> executeActionForth()
        }

        updateScreen()
    }

    fun onScreenClicked(lineIndex: Int, fieldIndex: Int) {
        if (lineIndex == 0) {
            resetCursorPosition()
            return
        }

        val stackIndex = lineIndex - 1
        resetSelectionIfDifferent(stackIndex, fieldIndex)
        fieldSelected = stackIndex to fieldIndex

        if (calculations[stackIndex].fieldSelected == -1)
            calculations[stackIndex] = calculations[stackIndex].copy(fieldSelected = fieldIndex)
        else copySelectionToWorkLine(fieldIndex, stackIndex)

        updateScreen()
    }

    fun restoreState(calcState: List<CalculationLine>) {
        if (calcState.isEmpty()) return
        currentCalculation = calcState[0]
        cursorPosition = currentCalculation.operation.length - 1
        calculations = calcState.drop(1).toMutableList()
        updateScreen()
    }

    private fun getKeyValue(keyCode: Int) = when (keyCode) {
        0 -> "0"
        1 -> "."
        3 -> ADD
        5 -> "1"
        6 -> "2"
        7 -> "3"
        8 -> SUB
        10 -> "4"
        11 -> "5"
        12 -> "6"
        13 -> MUL
        15 -> "7"
        16 -> "8"
        17 -> "9"
        18 -> DIV
        else -> null
    }

    private fun getKeyAction(keyCode: Int) = when (keyCode) {
        2 -> ANSWER
        4 -> ENTER
        20 -> ESC
        21 -> DELETE
        22 -> BACK
        23 -> FORTH
        else -> null
    }

    private fun processKeyValue(keyValue: String) {
        currentCalculation = currentCalculation.copy(
            operation = currentCalculation.operation.replaceFirst(
                CURSOR,
                "$keyValue${CURSOR}"
            )
        )
        cursorPosition++
    }

    private fun executeActionEsc() {
        fieldSelected?.let {
            calculations[it.first] = calculations[it.first].copy(fieldSelected = -1)
            fieldSelected = null
        } ?: run {
            currentCalculation = CalculationLine()
            cursorPosition = 0
        }
    }

    private fun executeActionDelete() {
        if (cursorPosition == 0) return
        currentCalculation = currentCalculation.copy(
            operation = currentCalculation.operation.removeRange(cursorPosition - 1, cursorPosition)
        )
        cursorPosition--
    }

    private fun executeActionEnter() {
        val noCursorOperation = currentCalculation.operation.replaceFirst(CURSOR, "")
        if (noCursorOperation.isEmpty()) return

        when (val calculationResult = solver.solve(noCursorOperation)) {
            is Success -> onSuccessResult(noCursorOperation, calculationResult.result.toString())
            is SyntaxError -> onSyntaxErrorResult(calculationResult.cursorPosition)
        }
    }

    private fun onSuccessResult(noCursorOperation: String, successResult: String) {
        val newResult = currentCalculation.copy(
            operation = noCursorOperation,
            result = successResult
        )
        calculations.add(0, newResult)
        currentCalculation = CalculationLine()
        cursorPosition = 0
    }

    private fun onSyntaxErrorResult(cursorPosition: Int) {
        this.cursorPosition = cursorPosition
        addCursor()
        errorState.value = SYNTAX_ERROR
    }

    private fun executeActionAnswer() {
        if (calculations.isEmpty()) return
        val value = calculations[0].result
        currentCalculation = currentCalculation.copy(
            operation = currentCalculation.operation.replaceFirst(
                CURSOR,
                "$value${CURSOR}"
            )
        )
        cursorPosition += value.length
    }

    private fun executeActionBack() {
        if (cursorPosition > 0) {
            cursorPosition--
            addCursor()
        }
    }

    private fun executeActionForth() {
        if (cursorPosition < currentCalculation.operation.length - 1) {
            cursorPosition++
            addCursor()
        }
    }

    private fun updateScreen() {
        calculationsState.value = listOf(currentCalculation) + calculations
    }

    private fun resetCursorPosition() {
        currentCalculation = currentCalculation.copy(
            operation = currentCalculation.operation.replaceFirst(CURSOR, "") + CURSOR
        )
        cursorPosition = currentCalculation.operation.length - 1
        updateScreen()
        return
    }

    private fun addCursor() {
        val noCursorOperation = currentCalculation.operation.replaceFirst(CURSOR, "")
        currentCalculation = currentCalculation.copy(
            operation = noCursorOperation.substring(0, cursorPosition) +
                    CURSOR +
                    noCursorOperation.substring(cursorPosition, noCursorOperation.length)
        )
    }

    private fun resetSelectionIfDifferent(stackIndex: Int, fieldIndex: Int) {
        fieldSelected?.let {
            if (it.first != stackIndex || it.second != fieldIndex) {
                calculations[it.first] = calculations[it.first].copy(fieldSelected = -1)
            }
        }
    }

    private fun copySelectionToWorkLine(fieldIndex: Int, stackIndex: Int) {
        val value =
            if (fieldIndex == 0) calculations[stackIndex].operation else calculations[stackIndex].result
        calculations[stackIndex] = calculations[stackIndex].copy(fieldSelected = -1)
        currentCalculation = currentCalculation.copy(
            operation = currentCalculation.operation.replaceFirst(
                CURSOR,
                "$value${CURSOR}"
            )
        )
        fieldSelected = null
        cursorPosition += value.length
    }
}