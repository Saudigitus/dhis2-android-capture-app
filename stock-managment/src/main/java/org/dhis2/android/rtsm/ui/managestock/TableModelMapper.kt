package org.dhis2.android.rtsm.ui.managestock

import org.dhis2.android.rtsm.data.models.StockEntry
import org.dhis2.composetable.model.RowHeader
import org.dhis2.composetable.model.TableCell
import org.dhis2.composetable.model.TableHeader
import org.dhis2.composetable.model.TableHeaderCell
import org.dhis2.composetable.model.TableHeaderRow
import org.dhis2.composetable.model.TableModel
import org.dhis2.composetable.model.TableRowModel
import javax.inject.Inject

class TableModelMapper @Inject constructor() {
    fun map(
        entries: List<StockEntry>,
        stockLabel: String,
        qtdLabel: String
    ): List<TableModel> {
        val tableRowModels = mutableListOf<TableRowModel>()

        entries.forEachIndexed { index, entry ->
            val item = entry.item
            val tableRowModel = TableRowModel(
                rowHeader = RowHeader(
                    id = item.id,
                    title = item.name,
                    row = index
                ),
                values = mapOf(
                    Pair(
                        0,
                        TableCell(
                            id = item.id,
                            row = index,
                            column = 0,
                            editable = false,
                            value = item.stockOnHand
                        )
                    ),
                    Pair(
                        1,
                        TableCell(
                            id = item.id,
                            row = index,
                            column = 1,
                            value = entry.qty,
                            editable = true
                        )
                    )
                ),
                maxLines = 3
            )

            tableRowModels.add(tableRowModel)
        }

        return mutableListOf(
            TableModel(
                id = "STOCK",
                tableHeaderModel = TableHeader(
                    rows = mutableListOf(
                        TableHeaderRow(
                            mutableListOf(
                                TableHeaderCell(stockLabel),
                                TableHeaderCell(qtdLabel)
                            )
                        )
                    )
                ),
                tableRows = tableRowModels,
            )
        )
    }
}