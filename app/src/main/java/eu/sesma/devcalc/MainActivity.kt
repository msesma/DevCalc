package eu.sesma.devcalc

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import eu.sesma.devcalc.Actions.*
import eu.sesma.devcalc.ui.theme.DevCalcTheme

enum class Actions {
    ANSWER, ENTER, CLEAR, DELETE, BACK, FORTH, COPY
}

class MainActivity : ComponentActivity() {

    companion object {
        const val CURSOR = "|"
    }

    private var currentCalculation = CalculationLine()
    private var calculations = mutableListOf<CalculationLine>()
    private val calculationsState = mutableStateOf(listOf(currentCalculation))
    private var cursorPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DevCalcTheme {
                CalcComposeView(
                    calculations = calculationsState,
                    onKeyClick = ::onKeyClicked,
                    onScreenClick = ::onScreenClicked
                )
            }
        }
    }

    private fun onScreenClicked(lineIndex: Int, fieldIndex: Int) {
        Log.d("==>", "Click line $lineIndex, field $fieldIndex")
    }

    private fun onKeyClicked(keyCode: Int) {
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
            2 -> ANSWER
            4 -> ENTER
            20 -> CLEAR
            21 -> DELETE
            22 -> BACK
            23 -> FORTH
            24 -> COPY
            else -> null
        }

        if (keyValue != null) {
            currentCalculation =
                currentCalculation.copy(
                    operation = currentCalculation.operation.replaceFirst(
                        CURSOR,
                        "$keyValue$CURSOR"
                    )
                )
            calculationsState.value = listOf(currentCalculation) + calculations
            cursorPosition++
        }

        if (keyAction != null) {

            when (keyAction) {
                CLEAR -> currentCalculation = CalculationLine()
                ENTER -> {
                    val noCursorOperation = currentCalculation.operation.replaceFirst(CURSOR, "")
                    val newResult = currentCalculation.copy(operation = noCursorOperation, result = noCursorOperation)
                    calculations.add(0, newResult)
                    currentCalculation = CalculationLine()
                }
                BACK -> if (cursorPosition > 0) {
                    cursorPosition--
                    addCursor()
                }
                FORTH -> if (cursorPosition < currentCalculation.operation.length - 1) {
                    cursorPosition++
                    addCursor()
                }
                else -> Unit
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
