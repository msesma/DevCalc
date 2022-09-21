package eu.sesma.devcalc

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import eu.sesma.devcalc.Editor.Companion.CURSOR

class CursorTransformation() : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val cursorPosition = text.indexOfFirst { it.toString() == CURSOR }
        if (cursorPosition == -1) return TransformedText(text, OffsetMapping.Identity)

        val transformedText = AnnotatedString(text.substring(0, cursorPosition))
            .plus(AnnotatedString(text = CURSOR, spanStyle = SpanStyle(color = Color.LightGray)))
            .plus(AnnotatedString(text.substring(cursorPosition + 1, text.length)))

        return TransformedText(transformedText, OffsetMapping.Identity)
    }
}