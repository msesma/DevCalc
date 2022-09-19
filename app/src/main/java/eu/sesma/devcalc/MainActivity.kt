package eu.sesma.devcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import eu.sesma.devcalc.ui.theme.DevCalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DevCalcTheme {
                CalcComposeView( calculations = listOf(CalculationLine(calculation = "125+500", result = "625"), CalculationLine(calculation = "25669882/5566", result = "2255")))
            }
        }
    }
}
