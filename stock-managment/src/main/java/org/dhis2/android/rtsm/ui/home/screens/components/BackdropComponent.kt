package org.dhis2.android.rtsm.ui.home.screens.components

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.dhis2.android.rtsm.data.TransactionType
import org.dhis2.android.rtsm.ui.home.HomeActivity
import org.dhis2.android.rtsm.ui.home.HomeViewModel
import org.dhis2.android.rtsm.ui.managestock.ManageStockViewModel

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun Backdrop(
    activity: Activity,
    viewModel: HomeViewModel,
    manageStockViewModel: ManageStockViewModel,
    themeColor: Color,
    supportFragmentManager: FragmentManager,
    homeContext: HomeActivity,
    barcodeLauncher: ActivityResultLauncher<ScanOptions>,
    scaffoldState: ScaffoldState,
    syncAction: (scope: CoroutineScope, scaffoldState: ScaffoldState) -> Unit = { _, _ -> }
) {
    val backdropState = rememberBackdropScaffoldState(BackdropValue.Concealed)

    var isFrontLayerDisabled by remember { mutableStateOf<Boolean?>(null) }
    var hasFacilitySelected by remember { mutableStateOf(false) }
    var hasDestinationSelected by remember { mutableStateOf<Boolean?>(null) }
    var toolbarTitle by remember {
        mutableStateOf(TransactionType.DISTRIBUTION.name)
    }
    var heightBackLayer by remember {
        mutableStateOf(0.dp)
    }
    val scope = rememberCoroutineScope()

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
            toolbarTitle = viewModel.toolbarTitle.collectAsState().value.name
        },
        backLayerBackgroundColor = themeColor,
        backLayerContent = {
            val height = filterList(
                viewModel,
                themeColor,
                supportFragmentManager,
                homeContext,
                { hasFacilitySelected = it },
                {
                    hasDestinationSelected = it
                    viewModel.setDestinationSelected(it)
                }
            )
            heightBackLayer = height
            if (height > 160.dp) {
                scope.launch { backdropState.reveal() }
            }
        },
        frontLayerElevation = 5.dp,
        frontLayerContent = {
            MainContent(
                backdropState,
                isFrontLayerDisabled,
                themeColor,
                viewModel,
                manageStockViewModel,
                hasFacilitySelected,
                hasDestinationSelected,
                barcodeLauncher,
                heightBackLayer
            )
        },
        scaffoldState = backdropState,
        gesturesEnabled = false,
        frontLayerScrimColor = if (toolbarTitle == TransactionType.DISTRIBUTION.name) {
            if (hasFacilitySelected && hasDestinationSelected == true) {
                isFrontLayerDisabled = false
                Color.Unspecified
            } else {
                isFrontLayerDisabled = true
                MaterialTheme.colors.surface.copy(alpha = 0.60f)
            }
        } else {
            if (!hasFacilitySelected) {
                isFrontLayerDisabled = true
                MaterialTheme.colors.surface.copy(alpha = 0.60f)
            } else {
                isFrontLayerDisabled = false
                Color.Unspecified
            }
        }
    )
}
