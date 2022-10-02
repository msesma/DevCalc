package eu.sesma.devcalc.editor

import eu.sesma.devcalc.editor.Constants.CURSOR


data class CalculationLine(
    val operation: String = CURSOR,
    val result: String = "",
    val fieldSelected: Int = -1
)