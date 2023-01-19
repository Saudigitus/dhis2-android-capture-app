package org.dhis2.android.rtsm.ui.home.model

import androidx.compose.ui.graphics.Color
import org.dhis2.android.rtsm.R
import org.dhis2.android.rtsm.ui.home.model.ButtonVisibilityState.HIDDEN

data class ButtonUiState(
    val text: Int = R.string.review,
    val icon: Int = R.drawable.proceed_icon,
    val color: Color = Color.White,
    val visibility: ButtonVisibilityState = HIDDEN
)

enum class ButtonVisibilityState {
    HIDDEN,
    DISABLED,
    ENABLED
}
