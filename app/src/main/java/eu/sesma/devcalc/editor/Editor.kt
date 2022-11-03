package eu.sesma.devcalc.editor

import androidx.compose.runtime.mutableStateOf
import eu.sesma.devcalc.editor.Constants.ADD
import eu.sesma.devcalc.editor.Constants.CURSOR
import eu.sesma.devcalc.editor.Constants.DEG
import eu.sesma.devcalc.editor.Constants.DIV
import eu.sesma.devcalc.editor.Constants.LBRKT
import eu.sesma.devcalc.editor.Constants.MINUS
import eu.sesma.devcalc.editor.Constants.MUL
import eu.sesma.devcalc.editor.Constants.POW
import eu.sesma.devcalc.editor.Constants.RAD
import eu.sesma.devcalc.editor.Constants.RBRKT
import eu.sesma.devcalc.editor.Constants.SUB
import eu.sesma.devcalc.editor.Constants.SYNTAX_ERROR
import eu.sesma.devcalc.editor.Editor.Actions.*
import eu.sesma.devcalc.solver.CalculationResult.Success
import eu.sesma.devcalc.solver.CalculationResult.SyntaxError
import eu.sesma.devcalc.solver.Solver
import kotlin.math.E
import kotlin.math.PI

class Editor(val solver: Solver) {

    enum class Actions {
        ANSWER, ENTER, ESC, CLEAR, DELETE, BACK, START, FORTH, END, NEGATE, SQUARE, INVERSE, BRACKETS,
        LN, LOG, SIN, ASIN, COS, ACOS, TAN, ATAN, MODE
    }

    private var currentCalculation = CalculationLine()
    private var calculations = mutableListOf<CalculationLine>()
    private var cursorPosition = 0
    private var fieldSelected: Pair<Int, Int>? = null

    val calculationsState = mutableStateOf(listOf(currentCalculation))
    val notificationsState = mutableStateOf(NotificationsLine())

    fun onKeyClicked(keyCode: Int) {
        notificationsState.value = notificationsState.value.copy(error = "")

        if (keyCode == 34) {
            notificationsState.value = notificationsState.value.copy(shifted = !notificationsState.value.shifted)
            return
        }

        val keyValue = getKeyValue(keyCode, notificationsState.value.shifted)
        val keyAction = getKeyAction(keyCode, notificationsState.value.shifted)

        notificationsState.value = notificationsState.value.copy(shifted = false)

        if (keyValue != null) processKeyValue(keyValue)
        if (keyAction != null) processKeyAction(keyAction)

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

    private fun getKeyValue(keyCode: Int, shifted: Boolean) = when (keyCode) {
        0 -> "0"
        1 -> "."
        3 -> ADD
        5 -> "1"
        6 -> if (shifted) E.toString() else "2"
        7 -> if (shifted) PI.toString() else "3"
        8 -> if (shifted) null else SUB
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

    private fun getKeyAction(keyCode: Int, shifted: Boolean) = when (keyCode) {
        2 -> ANSWER
        4 -> ENTER
        8 -> if (shifted) NEGATE else null
        14 -> INVERSE
        19 -> if (shifted) MODE else BRACKETS
        20 -> if (shifted) LOG else LN
        21 -> if (shifted) ASIN else SIN
        22 -> if (shifted) ACOS else COS
        23 -> if (shifted) ATAN else TAN
        24 -> SQUARE
        30 -> if (shifted) CLEAR else ESC
        31 -> DELETE
        32 -> if (shifted) START else BACK
        33 -> if (shifted) END else FORTH
        else -> null
    }

    private fun processKeyValue(keyValue: String) {
        addAtCursorPosition("$keyValue$CURSOR")
        cursorPosition++
    }

    private fun processKeyAction(keyAction: Actions) {
        when (keyAction) {
            ESC -> executeActionEsc()
            CLEAR -> executeActionClear()
            DELETE -> executeActionDelete()
            ENTER -> executeActionEnter()
            ANSWER -> executeActionAnswer()
            BACK -> executeActionBack()
            START -> executeActionBack(start = true)
            FORTH -> executeActionForth()
            END -> executeActionForth(end = true)
            NEGATE -> executeActionNegate()
            SQUARE -> executeActionConcatToOperand("${POW}2")
            INVERSE -> executeActionConcatToOperand("$POW${MINUS}1")
            BRACKETS -> executeActionAddBrackets()
            LN -> executeActionFunction(LN)
            LOG -> executeActionFunction(LOG)
            SIN -> executeActionFunction(SIN)
            ASIN -> executeActionFunction(ASIN)
            COS -> executeActionFunction(COS)
            ACOS -> executeActionFunction(ACOS)
            TAN -> executeActionFunction(TAN)
            ATAN -> executeActionFunction(ATAN)
            MODE -> executeActionMode()
        }
    }

    private fun executeActionMode() {
        val currentMode = notificationsState.value.mode
        notificationsState.value = notificationsState.value.copy(mode = if (currentMode == DEG) RAD else DEG)
    }

    private fun executeActionFunction(function: Actions) {
        addAtCursorPosition("$function$LBRKT$CURSOR$RBRKT")
        cursorPosition += function.toString().length
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

    private fun executeActionClear() {
        currentCalculation = CalculationLine()
        calculations = mutableListOf()
        cursorPosition = 0
        updateScreen()
    }

    private fun executeActionDelete() {
        if (cursorPosition == 0) return
        currentCalculation = currentCalculation.copy(
            operation = currentCalculation.operation.removeRange(cursorPosition - 1, cursorPosition)
        )
        cursorPosition--
    }

    private fun executeActionEnter() {
        var noCursorOperation = currentCalculation.operation.replaceFirst(CURSOR, "")
        if (noCursorOperation.isEmpty()) return

        noCursorOperation = solver.fixSyntax(noCursorOperation)
        when (val calculationResult = solver.solve(noCursorOperation, notificationsState.value.mode == RAD)) {
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
        notificationsState.value = notificationsState.value.copy(error = SYNTAX_ERROR)
    }

    private fun executeActionAnswer() {
        if (calculations.isEmpty()) return
        val value = calculations[0].result
        addAtCursorPosition("$value$CURSOR")
        cursorPosition += value.length
    }

    private fun executeActionBack(start: Boolean = false) {
        if (cursorPosition > 0) {
            cursorPosition = if (start) 0 else cursorPosition - 1
            addCursor()
        }
    }

    private fun executeActionForth(end: Boolean = false) {
        if (cursorPosition < currentCalculation.operation.length - 1) {
            cursorPosition = if (end) currentCalculation.operation.length - 1 else cursorPosition + 1
            addCursor()
        }
    }

    private fun executeActionNegate() {
        val currentOperand = currentCalculation.operation.split(ADD, SUB, MUL, DIV).first { it.contains(CURSOR) }
        val negatedOperand = if (currentOperand[0].toString() == MINUS) {
            cursorPosition--
            currentOperand.removePrefix(MINUS)
        } else {
            cursorPosition++
            MINUS + currentOperand
        }
        currentCalculation = CalculationLine(
            operation = currentCalculation.operation.replaceFirst(currentOperand, negatedOperand)
        )
        addCursor()
        updateScreen()
    }

    private fun executeActionConcatToOperand(addon: String) {
        val currentOperand = currentCalculation.operation.split(ADD, SUB, MUL, DIV).first { it.contains(CURSOR) }
        if (currentOperand.last() == CURSOR[0]) cursorPosition += addon.length
        val newOperand = currentOperand + addon
        currentCalculation = CalculationLine(
            operation = currentCalculation.operation.replaceFirst(currentOperand, newOperand)
        )
        addCursor()
        updateScreen()
    }

    private fun executeActionAddBrackets() {
        addAtCursorPosition("$LBRKT$CURSOR$RBRKT")
        cursorPosition++
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
        addAtCursorPosition("")
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
        addAtCursorPosition("$value$CURSOR")
        fieldSelected = null
        cursorPosition += value.length
    }

    private fun addAtCursorPosition(expression: String) {
        currentCalculation = currentCalculation.copy(
            operation = currentCalculation.operation.replaceFirst(CURSOR, expression)
        )
    }
}