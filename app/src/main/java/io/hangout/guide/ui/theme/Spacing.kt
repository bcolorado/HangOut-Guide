package io.hangout.guide.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data object SpacingDefaults {
    internal const val TINY = 2
    internal const val EXTRA_SMALL = 4
    internal const val SMALL = 8
    internal const val MEDIUM = 16
    internal const val LARGE = 32
    internal const val DEFAULT = SMALL
}

data class Spacing(
    val default: Dp = SpacingDefaults.DEFAULT.dp,
    val tiny: Dp = SpacingDefaults.TINY.dp,
    val extraSmall: Dp = SpacingDefaults.EXTRA_SMALL.dp,
    val small: Dp = SpacingDefaults.SMALL.dp,
    val medium: Dp = SpacingDefaults.MEDIUM.dp,
    val large: Dp = SpacingDefaults.LARGE.dp,
)