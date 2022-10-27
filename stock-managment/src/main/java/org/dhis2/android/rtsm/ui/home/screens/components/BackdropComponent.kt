package org.dhis2.android.rtsm.ui.home.screens.components

import android.app.Activity
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.material.composethemeadapter.MdcTheme
import kotlinx.coroutines.CoroutineScope
import org.dhis2.android.rtsm.data.TransactionType
import org.dhis2.android.rtsm.ui.home.HomeViewModel
import org.dhis2.android.rtsm.ui.managestock.ManageStockViewModel
import org.dhis2.android.rtsm.ui.managestock.components.ManageStockTable

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Backdrop(
    activity: Activity,
    viewModel: HomeViewModel,
    manageStockViewModel: ManageStockViewModel,
    themeColor: Color,
    scaffoldState: ScaffoldState,
    syncAction: (scope: CoroutineScope, scaffoldState: ScaffoldState) -> Unit = { _, _ -> }
) {
    val backdropState = rememberBackdropScaffoldState(BackdropValue.Concealed)

    var hasFacilitySelected by remember { mutableStateOf(false) }
    var hasDestinationSelected by remember { mutableStateOf<Boolean?>(null) }
    var hasFilterSelected by remember { mutableStateOf(false) }

    BackdropScaffold(
        appBar = {
            Toolbar(
                viewModel.toolbarTitle.collectAsState().value.name,
                viewModel.fromFacility.collectAsState().value.asString(),
                viewModel.deliveryTo.collectAsState().value?.asString(),
                themeColor,
                navigationAction = {
                    activity.finish()
                },
                backdropState,
                scaffoldState,
                syncAction,
                hasFacilitySelected,
                hasDestinationSelected
            )
        },
        backLayerBackgroundColor = themeColor,
        backLayerContent = {
            FilterList(
                viewModel, themeColor,
                {
                    hasFacilitySelected = it
                    updateTableState(hasFilterSelected, manageStockViewModel, viewModel)
                },
                {
                    hasDestinationSelected = it
                    updateTableState(hasFilterSelected, manageStockViewModel, viewModel)
                }
            )
        },
        frontLayerElevation = 5.dp,
        frontLayerContent = {
                if (viewModel.toolbarTitle.collectAsState().value.name
                    == TransactionType.DISTRIBUTION.name) {
                    if (hasFacilitySelected && hasDestinationSelected == true) {
                        hasFilterSelected = true
                        MdcTheme {
                            updateTableState(hasFilterSelected, manageStockViewModel, viewModel)
                            ManageStockTable(manageStockViewModel)
                        }
                    }
                } else if (hasFacilitySelected) {
                    hasFilterSelected = true
                    MdcTheme {
                        updateTableState(hasFilterSelected, manageStockViewModel, viewModel)
                        ManageStockTable(manageStockViewModel)
                    }
                }

        },
        scaffoldState = backdropState,
        gesturesEnabled = false,
        frontLayerScrimColor = Color.Unspecified
    )
}

private fun updateTableState(
    value: Boolean,
    manageStockViewModel: ManageStockViewModel,
    viewModel: HomeViewModel
) {
    if (value) {
        manageStockViewModel.setup(viewModel.getData())
    }
}
