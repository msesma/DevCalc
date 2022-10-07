package eu.sesma.devcalc.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import eu.sesma.devcalc.CalcComposeView
import eu.sesma.devcalc.CalcState.LineState
import eu.sesma.devcalc.editor.CalculationLine
import eu.sesma.devcalc.editor.Editor
import eu.sesma.devcalc.editor.calcStateDataStore
import eu.sesma.devcalc.solver.Solver
import eu.sesma.devcalc.ui.theme.DevCalcTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val solver = Solver()
    private val editor = Editor(solver = solver)
    private val exampleCalculationLineFlow: Flow<List<CalculationLine>>
        get() {
            return this.calcStateDataStore.data.map { calcState ->
                calcState.calcStateList.map { lineState ->
                    CalculationLine(operation = lineState.operation, result = lineState.result)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DevCalcTheme {
                CalcComposeView(
                    errorState = editor.errorState,
                    calculationsState = editor.calculationsState,
                    onKeyClick = editor::onKeyClicked,
                    onScreenClick = editor::onScreenClicked
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch { editor.restoreState(exampleCalculationLineFlow.first()) }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch { saveCalculationState(editor.calculationsState.value) }
    }

    private suspend fun saveCalculationState(calculationState: List<CalculationLine>) {
        this.calcStateDataStore.updateData { calcState ->
            val builder = calcState.toBuilder().clearCalcState()
            calculationState.forEach { calculationLine ->
                val lineState = LineState.newBuilder()
                    .setOperation(calculationLine.operation)
                    .setResult(calculationLine.result)
                    .build()
                builder.addCalcState(lineState)
            }
            builder.build()
        }
    }
}
