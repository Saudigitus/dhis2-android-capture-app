package org.dhis2.android.rtsm.ui.managestock.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.dhis2.android.rtsm.R
import org.dhis2.android.rtsm.ui.managestock.ManageStockViewModel
import org.dhis2.composetable.model.KeyboardInputType
import org.dhis2.composetable.model.TableCell
import org.dhis2.composetable.model.TableHeader
import org.dhis2.composetable.model.TableHeaderCell
import org.dhis2.composetable.model.TableHeaderRow
import org.dhis2.composetable.model.TableModel
import org.dhis2.composetable.model.TableRowModel
import org.dhis2.composetable.model.TextInputModel
import org.dhis2.composetable.ui.DataSetTableScreen


@Composable
private fun mapTableModel(
    stocks: List<TableRowModel>
) = mutableListOf(
    TableModel(
        id = "STOCK",
        tableHeaderModel = TableHeader(
            rows = mutableListOf(
                TableHeaderRow(
                    mutableListOf(
                        TableHeaderCell(stringResource(R.string.stock)),
                        TableHeaderCell(stringResource(R.string.quantity)),
                    )
                )
            )
        ),
        tableRows = stocks,
        upperPadding = false
    )
)

@Composable
fun ManageStockTable(
    viewModel: ManageStockViewModel
) {
    val context = LocalContext.current

    DataSetTableScreen(
        tableData = mapTableModel(
            viewModel.tableRowData(viewModel.getStockItems().observeAsState())
        ),
        onCellClick = { _, cell ->
            onCellClick(cell)
        },
        onEdition = { isEditing ->
            editingCellValue(context, isEditing)
        },
        onCellValueChange = { cell ->
            onCellValueChanged(cell)
        },
        onSaveValue = { cell ->
            onSaveValueChange(cell)
        }
    )
}

fun editingCellValue(context: Context, editing: Boolean) {}

fun onCellClick(cell: TableCell): TextInputModel {
    return TextInputModel(
        id = cell.id!!,
        mainLabel = "Quantity",
        currentValue = cell.value,
        keyboardInputType = KeyboardInputType.NumericInput(
            allowDecimal = false,
            allowSigned = false
        )
    )
}
fun onCellValueChanged(tableCell: TableCell) {}
fun onSaveValueChange(cell: TableCell) {}