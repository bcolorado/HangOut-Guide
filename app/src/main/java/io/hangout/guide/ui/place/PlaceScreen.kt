package io.hangout.guide.ui.place

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun PlaceScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Two Screen", fontSize = 20.sp)
    }
}