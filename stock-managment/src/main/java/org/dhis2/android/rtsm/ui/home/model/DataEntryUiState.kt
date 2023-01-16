package org.dhis2.android.rtsm.ui.home.model

data class DataEntryUiState(
    val step: DataEntryStep = DataEntryStep.EDITING,
    val button: ButtonUiState = ButtonUiState(),
    val hasUnsavedData: Boolean = false,
    val dialogMsg:String = ""
)

enum class DataEntryStep {
    LISTING,
    EDITING,
    REVIEWING,
    COMPLETED
}
