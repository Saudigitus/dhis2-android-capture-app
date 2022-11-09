package org.dhis2.android.rtsm.ui.managestock.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.android.material.composethemeadapter.MdcTheme
import org.dhis2.android.rtsm.R
import org.dhis2.android.rtsm.ui.managestock.ManageStockViewModel
import org.dhis2.composetable.model.TableCell
import org.dhis2.composetable.ui.DataSetTableScreen
import timber.log.Timber

@Composable
fun ManageStockTable(
    viewModel: ManageStockViewModel
) {
    val context = LocalContext.current

    MdcTheme {
        DataSetTableScreen(
            tableData = viewModel.tableRowData(
                viewModel.getStockItems().observeAsState(),
                stringResource(R.string.stock),
                stringResource(R.string.quantity)
            ),
            onCellClick = { _, cell ->
                Timber.tag("ROW").e("${cell.row}")
                Timber.tag("COL").e("${cell.column}")

                viewModel.onCellClick(cell)
            },
            onEdition = { isEditing ->
                editingCellValue(context, isEditing)
            },
            onCellValueChange = { cell ->
                viewModel.onCellValueChanged(cell)
            },
            onSaveValue = { cell ->
                viewModel.onSaveValueChange(cell)
            }
        )
    }
}

fun editingCellValue(context: Context, editing: Boolean) {}
fun onSaveValueChange(cell: TableCell) {
    Timber.tag("TBV").e(cell.value)
}
