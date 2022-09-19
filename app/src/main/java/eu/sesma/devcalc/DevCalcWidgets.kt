package eu.sesma.devcalc

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.sesma.devcalc.ui.theme.DevCalcTheme

@Composable
fun Key(
    modifier: Modifier = Modifier,
    keyCode: Int,
    @DrawableRes icon: Int? = null,
    text: String = "",
    onClick: (Int) -> Unit,
) {
    Button(
        onClick = { onClick(keyCode) },
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background),
        modifier = modifier
            .requiredSize(64.dp)
            .semantics { testTag = "key_$keyCode" },
        elevation = ButtonDefaults.elevation(0.dp),
        contentPadding = PaddingValues(all = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface)
        ) {
            if (icon != null) Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .semantics { testTag = "delete_button" },
                painter = painterResource(icon),
                tint = MaterialTheme.colors.onSurface,
                contentDescription = ""
            ) else Text(
                modifier = modifier.align(Alignment.Center),
                text = text,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

@Preview
@Composable
fun PinButtonPreview() {
    DevCalcTheme {
        Key(keyCode = 1, text = "2", onClick = {})
    }
}

@Composable
fun PinPanel(
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit = {},
) {
    Column(
        modifier = modifier.height(320.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val width = 320.dp
        Row(
            modifier = Modifier.width(width),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Key(keyCode = 20, onClick = onClick, text = "AC")
            Key(keyCode = 21, onClick = onClick, text = "C")
            Key(keyCode = 22, onClick = onClick, text = "<-")
            Key(keyCode = 23, onClick = onClick, text = "->")
            Key(keyCode = 24, onClick = onClick, text = "Cpy")
        }
        Row(
            modifier = Modifier.width(width),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Key(keyCode = 15, onClick = onClick, text = "7")
            Key(keyCode = 16, onClick = onClick, text = "8")
            Key(keyCode = 17, onClick = onClick, text = "0")
            Key(keyCode = 18, onClick = onClick, text = "/")
            Key(keyCode = 19, onClick = onClick, text = "")
        }
        Row(
            modifier = Modifier.width(width),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Key(keyCode = 10, onClick = onClick, text = "4")
            Key(keyCode = 11, onClick = onClick, text = "5")
            Key(keyCode = 12, onClick = onClick, text = "6")
            Key(keyCode = 13, onClick = onClick, text = "x")
            Key(keyCode = 14, onClick = onClick, text = "")
        }
        Row(
            modifier = Modifier.width(width),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Key(keyCode = 5, onClick = onClick, text = "1")
            Key(keyCode = 6, onClick = onClick, text = "2")
            Key(keyCode = 7, onClick = onClick, text = "3")
            Key(keyCode = 8, onClick = onClick, text = "-")
            Key(keyCode = 9, onClick = onClick, text = "")
        }
        Row(
            modifier = Modifier.width(width),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Key(keyCode = 0, onClick = onClick, text = "0")
            Key(keyCode = 1, onClick = onClick, text = ".")
            Key(keyCode = 2, onClick = onClick, text = "Ans")
            Key(keyCode = 3, onClick = onClick, text = "+")
            Key(keyCode = 4, onClick = onClick, text = "=")
        }
    }
}

@Preview
@Composable
fun PinPanelPreview() {
    DevCalcTheme {
        PinPanel()
    }
}

@Composable
fun ScreenItem(
    modifier: Modifier = Modifier,
    calculationLine: CalculationLine,
    isEditLine: Boolean,
) {
    val padding = 32.dp
    val paddingPx = with(LocalDensity.current) { padding.toPx() }
    val maxWidth = LocalContext.current.resources.displayMetrics.widthPixels - paddingPx * 2
    var calculationWidth = 0
    var resultWidth = 0
    var doubleLine by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CompositionLocalProvider(
                LocalTextInputService provides null
            ) {
                BasicTextField(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .horizontalScroll(enabled = false, state = ScrollState(initial = 0)),
                    value = calculationLine.calculation,
                    onValueChange = { },
                    readOnly = !isEditLine,
                    singleLine = true,
                    maxLines = 2,
                    onTextLayout = {
                        calculationWidth = it.size.width
                        doubleLine = calculationWidth + resultWidth > maxWidth
                    })
            }
            AnimatedVisibility(
                visible = !doubleLine,
                exit = fadeOut(animationSpec = keyframes { durationMillis = 0 })
            ) {
                BasicTextField(
                    modifier = Modifier.padding(end = 4.dp),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                    value = calculationLine.result,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    onTextLayout = {
                        resultWidth = it.size.width
                        doubleLine = calculationWidth + resultWidth > maxWidth
                    })
            }
        }
        AnimatedVisibility(visible = doubleLine) {
            BasicTextField(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(end = 4.dp),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                value = calculationLine.result,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                onTextLayout = {})
        }
    }
}

@Preview
@Composable
fun ScreenItemPreviewShort() {
    DevCalcTheme {
        ScreenItem(
            calculationLine = CalculationLine(calculation = "125+500", result = "625"),
            isEditLine = false
        )
    }
}

@Composable
fun ScreenList(
    modifier: Modifier = Modifier,
    calculations: List<CalculationLine>,
) {
    val scrollState = rememberLazyListState()
    LazyColumn(
        modifier = modifier
            .padding(top = 32.dp, start = 32.dp, end = 32.dp)
            .fillMaxWidth()
            .height(240.dp)
            .animateContentSize()
            .border(width = 1.dp, color = Color.DarkGray),
        state = scrollState,
        reverseLayout = true,
    ) {
        itemsIndexed(calculations) { index, calculation ->
            ScreenItem(calculationLine = calculation, isEditLine = index == 0)
            Divider(thickness = 1.dp, color = Color.DarkGray)
        }
    }
}

@Composable
fun CalcComposeView(
    modifier: Modifier = Modifier.fillMaxSize(),
    calculations: MutableState<List<CalculationLine>>,
    onClick: (Int) -> Unit = {},
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colors.onBackground
    ) {
        Column(
            modifier = modifier
                .background(color = MaterialTheme.colors.background)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScreenList(calculations = calculations.value)
            Surface(modifier = Modifier.weight(1f)) {}
            PinPanel(modifier = Modifier.padding(bottom = 32.dp), onClick = onClick)
        }
    }
}

@Preview(device = Devices.PIXEL_3_XL)
@Composable
fun CalcComposeViewPreview() {
    DevCalcTheme {
        CalcComposeView(
            calculations = mutableStateOf(
                listOf(
                    CalculationLine(calculation = "125+500", result = "625"),
                    CalculationLine(calculation = "25669882/5566", result = "2255")
                )
            )
        )
    }
}