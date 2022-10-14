package eu.sesma.devcalc.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.sesma.devcalc.editor.CalculationLine
import eu.sesma.devcalc.editor.Constants.ADD
import eu.sesma.devcalc.editor.Constants.DIV
import eu.sesma.devcalc.editor.Constants.MUL
import eu.sesma.devcalc.editor.Constants.PLM
import eu.sesma.devcalc.editor.Constants.SHIFT
import eu.sesma.devcalc.editor.Constants.SUB
import eu.sesma.devcalc.editor.NotificationsLine
import eu.sesma.devcalc.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun Key(
    modifier: Modifier = Modifier,
    keyCode: Int,
    shift: Boolean = false,
    text: String = "",
    secondaryText: String = "",
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
                .clip(shape = MaterialTheme.shapes.medium)
                .background(MaterialTheme.colors.surface)
                .border(width = 1.dp, color = KeyGrey),

            ) {
            Text(
                modifier = modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(color = if (shift) Color.Blue else KeyGrey),
                text = text,
                textAlign = TextAlign.Center,
                style = if (shift) MaterialTheme.typography.h6 else MaterialTheme.typography.h5,
                fontWeight = if (shift) FontWeight.Normal else FontWeight.Bold,
                color = if (shift) Color.White else MaterialTheme.colors.onSurface
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .height(4.dp),
                color = Color.LightGray
            ) {}
            Text(
                modifier = modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(20.dp)
                    .padding(bottom = 2.dp)
                    .background(color = KeyGreyAngle),
                text = secondaryText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold,
                color = Color.Blue
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
fun KeyPanel(
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
            Key(keyCode = 20, onClick = onClick, text = "Esc", secondaryText = "Clear")
            Key(keyCode = 21, onClick = onClick, text = "Del")
            Key(keyCode = 22, onClick = onClick, text = "<-", secondaryText = "|<-")
            Key(keyCode = 23, onClick = onClick, text = "->", secondaryText = "->|")
            Key(keyCode = 24, onClick = onClick, text = SHIFT, shift = true)
        }
        Row(
            modifier = Modifier.width(width),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Key(keyCode = 15, onClick = onClick, text = "7")
            Key(keyCode = 16, onClick = onClick, text = "8")
            Key(keyCode = 17, onClick = onClick, text = "9")
            Key(keyCode = 18, onClick = onClick, text = DIV)
            Key(keyCode = 19, onClick = onClick)
        }
        Row(
            modifier = Modifier.width(width),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Key(keyCode = 10, onClick = onClick, text = "4")
            Key(keyCode = 11, onClick = onClick, text = "5")
            Key(keyCode = 12, onClick = onClick, text = "6")
            Key(keyCode = 13, onClick = onClick, text = MUL)
            Key(keyCode = 14, onClick = onClick)
        }
        Row(
            modifier = Modifier.width(width),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Key(keyCode = 5, onClick = onClick, text = "1")
            Key(keyCode = 6, onClick = onClick, text = "2")
            Key(keyCode = 7, onClick = onClick, text = "3", secondaryText = "π")
            Key(keyCode = 8, onClick = onClick, text = SUB, secondaryText = PLM)
            Key(keyCode = 9, onClick = onClick)
        }
        Row(
            modifier = Modifier.width(width),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Key(keyCode = 0, onClick = onClick, text = "0")
            Key(keyCode = 1, onClick = onClick, text = ".")
            Key(keyCode = 2, onClick = onClick, text = "Ans")
            Key(keyCode = 3, onClick = onClick, text = ADD)
            Key(keyCode = 4, onClick = onClick, text = "=")
        }
    }
}

@Preview
@Composable
fun KeyPanelPreview() {
    DevCalcTheme {
        KeyPanel()
    }
}

@Composable
fun ScreenItem(
    modifier: Modifier = Modifier,
    calculationLine: CalculationLine,
    lineIndex: Int,
    onClick: (Int, Int) -> Unit
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
            .background(color = if (lineIndex == 0) WhiteTransparent else Color.Unspecified)
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
                        .horizontalScroll(
                            enabled = true,
                            state = ScrollState(initial = 0),
                            reverseScrolling = lineIndex == 0
                        )
                        .clickable { onClick(lineIndex, 0) }
                        .then(Modifier.background(if (calculationLine.fieldSelected == 0) Color.Cyan else Color.Unspecified)),
                    value = calculationLine.operation,
                    textStyle = LocalTextStyle.current,
                    onValueChange = { },
                    readOnly = true,
                    singleLine = true,
                    visualTransformation = CursorTransformation(),
                    enabled = false,
                    onTextLayout = {
                        calculationWidth = (it.size.width * 1.05).toInt()
                        doubleLine = calculationWidth + resultWidth > maxWidth && calculationLine.result.isNotEmpty()
                    })
            }
            AnimatedVisibility(
                visible = !doubleLine,
                exit = fadeOut(animationSpec = keyframes { durationMillis = 0 })
            ) {
                BasicTextField(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .clickable { onClick(lineIndex, 1) }
                        .then(Modifier.background(if (calculationLine.fieldSelected == 1) Color.Cyan else Color.Unspecified)),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                    value = calculationLine.result,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    enabled = false,
                    onTextLayout = {
                        resultWidth = it.size.width
                        doubleLine = calculationWidth + resultWidth > maxWidth && calculationLine.result.isNotEmpty()
                    })
            }
        }
        AnimatedVisibility(visible = doubleLine) {
            BasicTextField(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(end = 4.dp)
                    .horizontalScroll(
                        enabled = true,
                        state = ScrollState(initial = 0),
                        reverseScrolling = false
                    )
                    .clickable { onClick(lineIndex, 1) }
                    .then(Modifier.background(if (calculationLine.fieldSelected == 1) Color.Cyan else Color.Transparent)),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                value = calculationLine.result,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                enabled = false,
                onTextLayout = {})
        }
    }
}

@Preview
@Composable
fun ScreenItemPreviewShort() {
    DevCalcTheme {
        ScreenItem(
            calculationLine = CalculationLine(operation = "125+500", result = "625"),
            lineIndex = 0,
            onClick = { _, _ -> }
        )
    }
}

@Composable
fun Indicators(
    modifier: Modifier = Modifier,
    notifications: NotificationsLine,
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(16.dp)
            .background(color = WhiteTransparent)
            .drawBehind {
                val y = size.height - 1
                drawLine(color = Color.DarkGray, start = Offset(0f, y), end = Offset(size.width, y))
            }
            .padding(start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(bottom = 1.dp),
            text = notifications.error,
            color = Color.Red,
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.Medium,
        )
        Text(
            modifier = Modifier.padding(bottom = 1.dp),
            text = if (notifications.shifted) SHIFT else "",
            color = Color.Blue,
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Preview(device = Devices.PIXEL_3_XL)
@Composable
fun IndicatorsPreview() {
    DevCalcTheme {
        Indicators(notifications = NotificationsLine(error = "Syntax Error"))
    }
}

@Composable
fun Screen(
    modifier: Modifier = Modifier,
    calculations: List<CalculationLine>,
    notifications: NotificationsLine,
    scrollState: LazyListState,
    onClick: (Int, Int) -> Unit
) {
    Column(
        modifier = modifier
            .padding(top = 32.dp, start = 32.dp, end = 32.dp)
            .fillMaxWidth()
            .height(240.dp)
            .border(width = 1.dp, color = MaterialTheme.colors.onBackground)
            .background(color = LcdColor),
    ) {
        Indicators(notifications = notifications)
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f)
                .animateContentSize(),
            state = scrollState,
            reverseLayout = true,
        ) {
            itemsIndexed(calculations.drop(1)) { index, calculation ->
                ScreenItem(calculationLine = calculation, lineIndex = index + 1, onClick = onClick)
                Divider(thickness = 1.dp, color = Color.LightGray)
            }
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color.DarkGray
        ) {}
        ScreenItem(
            modifier = Modifier.fillMaxWidth(),
            calculationLine = calculations[0],
            lineIndex = 0,
            onClick = onClick
        )
    }
}

@Composable
fun CalcComposeView(
    modifier: Modifier = Modifier,
    notificationsState: MutableState<NotificationsLine>,
    calculationsState: MutableState<List<CalculationLine>>,
    onKeyClick: (Int) -> Unit,
    onScreenClick: (Int, Int) -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colors.onBackground
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val coroutineScope = rememberCoroutineScope()
            val scrollState = rememberLazyListState()
            Screen(
                calculations = calculationsState.value,
                notifications = notificationsState.value,
                scrollState = scrollState,
                onClick = onScreenClick
            )
            Surface(modifier = Modifier.weight(1f)) {}
            KeyPanel(
                modifier = Modifier.padding(bottom = 32.dp),
                onClick = { keyCode ->
                    coroutineScope.launch { scrollState.animateScrollToItem(0, 0) }
                    onKeyClick(keyCode)
                }
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(device = Devices.PIXEL_3_XL)
@Composable
fun CalcComposeViewPreview() {
    DevCalcTheme {
        CalcComposeView(
            notificationsState = mutableStateOf(NotificationsLine(error = "Syntax Error")),
            calculationsState = mutableStateOf(
                listOf(
                    CalculationLine(operation = "125+500", result = "625"),
                    CalculationLine(operation = "25669882/5566", result = "2255")
                )
            ),
            onKeyClick = {},
            onScreenClick = { _, _ -> }
        )
    }
}