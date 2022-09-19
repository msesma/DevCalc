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

    private var currentCalculation = CalculationLine()
    private var calculations = mutableListOf<CalculationLine>()
    private val calculationsState = mutableStateOf(listOf(currentCalculation))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DevCalcTheme {
                CalcComposeView(
                    calculations = calculationsState,
                    onClick = ::onKeyClicked
                )
            }
        }
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
                currentCalculation.copy(calculation = currentCalculation.calculation + keyValue)
            calculationsState.value = listOf(currentCalculation) + calculations
            Log.d("==>", "currentCalculation = $currentCalculation")
        }

        if (keyAction != null) {
            when (keyAction) {
                CLEAR -> currentCalculation = CalculationLine()
                ENTER -> {
                    val newResult = currentCalculation.copy(result = currentCalculation.calculation)
                    calculations.add(0, newResult)
                    currentCalculation = CalculationLine()
                }
                else -> Unit
            }
            calculationsState.value = listOf(currentCalculation) + calculations
        }

    }
}
