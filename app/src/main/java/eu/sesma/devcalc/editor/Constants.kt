package eu.sesma.devcalc.editor

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

object Constants {
    private val superscript = SpanStyle(
        baselineShift = BaselineShift.Superscript,
        fontSize = 16.sp,
    )

    const val CURSOR = "|"
    const val ADD = "+"
    const val SUB = "−"
    const val MUL = "×"
    const val DIV = "÷"
    const val MINUS = "-"
    const val PLM = "±"
    const val LBRKT = "("
    const val RBRKT = ")"
    const val POW = "^"
    const val ROOT ="√"
    val INV = buildAnnotatedString {
        append("x")
        withStyle( superscript) { append("-1") }
    }
    val SQR = buildAnnotatedString {
        append("x")
        withStyle( superscript) { append("2") }
    }

    const val OPERANDS = ADD + SUB + MUL + DIV

    const val SYNTAX_ERROR = "Syntax error"
    const val SHIFT = "Shift"
}