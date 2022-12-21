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
import org.dhis2.android.rtsm.R
import org.dhis2.android.rtsm.data.TransactionType
import org.dhis2.android.rtsm.ui.home.HomeActivity
import org.dhis2.android.rtsm.ui.home.HomeViewModel
import org.dhis2.android.rtsm.ui.managestock.ManageStockViewModel
import org.dhis2.commons.dialogs.bottomsheet.BottomSheetDialog
import org.dhis2.commons.dialogs.bottomsheet.BottomSheetDialogUiModel
import org.dhis2.commons.dialogs.bottomsheet.DialogButtonStyle

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
    val scope = rememberCoroutineScope()

    BackdropScaffold(
        appBar = {
            Toolbar(
                viewModel.toolbarTitle.collectAsState().value.name,
                viewModel.fromFacility.collectAsState().value.asString(),
                viewModel.deliveryTo.collectAsState().value?.asString(),
                themeColor,
                launchBottomSheet = {
                    if (manageStockViewModel.canReview()) {
                        launchBottomSheet(
                            activity.getString(R.string.not_saved),
                            activity.getString(R.string.transaction_not_confirmed),
                            supportFragmentManager
                        ) {
                            activity.finish()
                        }
                    } else {
                        activity.finish()
                    }
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
                barcodeLauncher
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

private fun launchBottomSheet(
    title: String,
    subtitle: String,
    supportFragmentManager: FragmentManager,
    navigationAction: () -> Unit
) {
    BottomSheetDialog(
        bottomSheetDialogUiModel = BottomSheetDialogUiModel(
            title = title,
            subtitle = subtitle,
            iconResource = R.drawable.ic_outline_error_36,
            mainButton = DialogButtonStyle.MainButton(org.dhis2.commons.R.string.keep_editing),
            secondaryButton = DialogButtonStyle.DiscardButton()
        ),
        onMainButtonClicked = { supportFragmentManager.popBackStack() },
        onSecondaryButtonClicked = { navigationAction.invoke() }
    ).apply {
        this.show(supportFragmentManager.beginTransaction(), "DIALOG")
    }
}
