package com.example.deviceexplorerapp.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.deviceexplorerapp.ui.theme.PurpleGrey40

@Composable
fun Separator(modifier: Modifier = Modifier, horizontal: Boolean = true) {
    if (horizontal) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = PurpleGrey40)
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(color = PurpleGrey40)
        )
    }
}