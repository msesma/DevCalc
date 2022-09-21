package eu.sesma.devcalc

import eu.sesma.devcalc.MainActivity.Companion.CURSOR

data class CalculationLine(
    val operation: String = CURSOR,
    val result: String = "",
    val fieldSelected: Int = -1
)