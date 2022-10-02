package eu.sesma.devcalc.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import eu.sesma.devcalc.CalcComposeView
import eu.sesma.devcalc.editor.Editor
import eu.sesma.devcalc.solver.Solver
import eu.sesma.devcalc.ui.theme.DevCalcTheme

class MainActivity : ComponentActivity() {

    private val solver = Solver()
    private val editor = Editor(solver = solver)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DevCalcTheme {
                CalcComposeView(
                    calculations = editor.calculationsState,
                    onKeyClick = editor::onKeyClicked,
                    onScreenClick = editor::onScreenClicked
                )
            }
        }
    }
}
