package io.hangout.guide.ui.theme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun outlinedTextFieldColors() = TextFieldDefaults.outlinedTextFieldColors(
    focusedBorderColor = Color(0xFF3F51B5), // Color del borde cuando está enfocado
    unfocusedBorderColor = Color.Black, // Color del borde cuando NO está enfocado
    cursorColor = Color(0xFF3F51B5), // Color del cursor
    focusedLabelColor = Color(0xFF3F51B5), // Color del texto del label cuando está enfocado
    unfocusedLabelColor = Color.Gray, // Color del texto del label cuando NO está enfocado
)
