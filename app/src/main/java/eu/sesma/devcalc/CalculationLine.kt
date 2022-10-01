package eu.sesma.devcalc

import eu.sesma.devcalc.Constants.CURSOR


data class CalculationLine(
    val operation: String = CURSOR,
    val result: String = "",
    val fieldSelected: Int = -1
)