package org.dhis2.android.rtsm.ui.home.model

data class DataEntryUiState(
    val step: DataEntryStep = DataEntryStep.START,
    val button: ButtonUiState = ButtonUiState(),
    val hasUnsavedData: Boolean = false
)

enum class DataEntryStep {
    START,
    LISTING,
    EDITING,
    REVIEWING,
    COMPLETED
}
