package com.example.deviceexplorerapp.ui.utils


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Ugly copy/paste from nordic code to allow quick handling of BLE permission
 */

@Composable
fun WarningView(
    imageVector: ImageVector,
    title: String,
    hint: String,
    modifier: Modifier = Modifier,
    hintTextAlign: TextAlign? = TextAlign.Center,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        BigIcon(imageVector = imageVector)

        Title(text = title)

        Hint(text = hint, textAlign = hintTextAlign)

        content()
    }
}

@Composable
fun WarningView(
    imageVector: ImageVector,
    title: String,
    hint: AnnotatedString,
    modifier: Modifier = Modifier,
    hintTextAlign: TextAlign? = TextAlign.Center,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BigIcon(imageVector = imageVector)

        Title(text = title)

        Hint(text = hint, textAlign = hintTextAlign)

        content()
    }
}

@Composable
fun WarningView(
    painterResource: Painter,
    title: String,
    hint: String,
    modifier: Modifier = Modifier,
    hintTextAlign: TextAlign? = TextAlign.Center,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BigIcon(painterResource = painterResource)

        Title(text = title)

        Hint(text = hint, textAlign = hintTextAlign)

        content()
    }
}


@Composable
internal fun BigIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    color: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
) {
    Image(
        imageVector = imageVector,
        contentDescription = null,
        modifier = modifier.size(size),
        colorFilter = ColorFilter.tint(color),
    )
}

@Composable
internal fun BigIcon(
    painterResource: Painter,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    color: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
) {
    Image(
        painter = painterResource,
        contentDescription = null,
        modifier = modifier.size(size),
        colorFilter = ColorFilter.tint(color),
    )
}

@Composable
internal fun Title(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
    style: TextStyle = MaterialTheme.typography.titleMedium,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = style,
    )
}


@Composable
internal fun Hint(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    textAlign: TextAlign? = TextAlign.Center,
) {
    Text(
        text = text,
        color = color,
        style = style,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
internal fun Hint(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    textAlign: TextAlign? = TextAlign.Center,
) {
    Text(
        text = text,
        color = color,
        style = style,
        modifier = modifier,
        textAlign = textAlign
    )
}