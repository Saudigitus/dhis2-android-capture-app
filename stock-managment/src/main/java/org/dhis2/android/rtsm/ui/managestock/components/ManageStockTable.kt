package org.dhis2.android.rtsm.ui.managestock.components

import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.google.android.material.composethemeadapter.MdcTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.dhis2.android.rtsm.ui.managestock.ManageStockViewModel
import org.dhis2.composetable.ui.DataSetTableScreen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ManageStockTable(
    viewModel: ManageStockViewModel,
    backdropState: BackdropScaffoldState,
    scope: CoroutineScope
) {
    val screenState by viewModel.screenState.observeAsState()

    MdcTheme {
        DataSetTableScreen(
            tableScreenState = screenState!!,
            onCellClick = { _, cell ->
                viewModel.onCellClick(cell)
            },
            onEdition = { isEditing ->
                editingCellValue(isEditing, backdropState, scope)
            },
            onCellValueChange = viewModel::onCellValueChanged,
            onSaveValue = viewModel::onSaveValueChange
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
fun editingCellValue(
    editing: Boolean,
    backdropState: BackdropScaffoldState,
    scope: CoroutineScope
) {
    // TODO Hide review button
    if (editing) {
        scope.launch { backdropState.conceal() }
    }
}
