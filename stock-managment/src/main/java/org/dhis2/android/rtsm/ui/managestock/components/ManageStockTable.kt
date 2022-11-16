package org.dhis2.android.rtsm.ui.managestock.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import com.google.android.material.composethemeadapter.MdcTheme
import org.dhis2.android.rtsm.R
import org.dhis2.android.rtsm.ui.managestock.ManageStockViewModel
import org.dhis2.composetable.ui.DataSetTableScreen


@Composable
fun ManageStockTable(
    viewModel: ManageStockViewModel
) {

    val screenState by viewModel.screenState.observeAsState()


    MdcTheme {
        DataSetTableScreen(
            tableScreenState = screenState!!,
            onCellClick = { _, cell ->
                viewModel.onCellClick(cell)
            },
            onEdition = ::editingCellValue,
            onCellValueChange = viewModel::onCellValueChanged,
            onSaveValue = viewModel::onSaveValueChange
        )

        viewModel.tableRowData(
            stringResource(R.string.stock),
            stringResource(R.string.quantity)
        )
    }
}

fun editingCellValue(editing: Boolean) {}
