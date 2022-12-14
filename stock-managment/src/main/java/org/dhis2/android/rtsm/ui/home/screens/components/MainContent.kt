package org.dhis2.android.rtsm.ui.home.screens.components

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import org.dhis2.android.rtsm.R
import org.dhis2.android.rtsm.data.TransactionType
import org.dhis2.android.rtsm.ui.home.HomeViewModel
import org.dhis2.android.rtsm.ui.managestock.ManageStockViewModel
import org.dhis2.android.rtsm.ui.managestock.components.ManageStockTable
import org.dhis2.android.rtsm.ui.scanner.ScannerActivity

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainContent(
    backdropState: BackdropScaffoldState,
    isFrontLayerDisabled: Boolean?,
    themeColor: Color,
    viewModel: HomeViewModel,
    manageStockViewModel: ManageStockViewModel,
    hasFacilitySelected: Boolean,
    hasDestinationSelected: Boolean?,
    barcodeLauncher: ActivityResultLauncher<ScanOptions>
) {
    val scope = rememberCoroutineScope()
    val resource = painterResource(R.drawable.ic_arrow_up)
    val qrcodeResource = painterResource(R.drawable.ic_qr_code_scanner)
    val searchResource = painterResource(R.drawable.ic_search)
    val closeResource = painterResource(R.drawable.ic_close)
    var closeButtonVisibility by remember { mutableStateOf(0f) }
    val weightValue = if (backdropState.isRevealed) 0.15f else 0.10f
    val weightValueArrow = if (backdropState.isRevealed) 0.10f else 0.05f
    val weightValueArrowStatus = backdropState.isRevealed
    val focusManager = LocalFocusManager.current
    val search = viewModel.scanText.collectAsState().value
    val localDensity = LocalDensity.current
    var heightIs by remember {
        mutableStateOf(0.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { coordinates ->
                heightIs = with(localDensity) { coordinates.height.toDp() }
            }
            .onGloballyPositioned { coordinates ->
                heightIs = with(localDensity) { coordinates.size.height.toDp() }
            }
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .absolutePadding(
                    left = 16.dp,
                    top = 16.dp,
                    right = 16.dp
                )
                .fillMaxWidth()
                .size(60.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = viewModel::setScannedText,
                modifier = Modifier
                    .background(Color.White, shape = CircleShape)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(30.dp),
                        clip = false
                    )
                    .offset(0.dp, 0.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(30.dp))
                    .weight(1 - (weightValue + weightValueArrow))
                    .alignBy(FirstBaseline)
                    .align(alignment = Alignment.CenterVertically),
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    cursorColor = themeColor
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                enabled = isFrontLayerDisabled != true,
                leadingIcon = {
                    Icon(
                        painter = searchResource,
                        contentDescription = "",
                        tint = themeColor
                    )
                },
                trailingIcon = {
                    IconButton(
                        modifier = Modifier
                            .alpha(closeButtonVisibility),
                        onClick = {
                            viewModel.setScannedText("")
                            closeButtonVisibility = 0f
                        }
                    ) {
                        Icon(
                            painter = closeResource,
                            contentDescription = ""
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        manageStockViewModel.onSearchQueryChanged(search)
                    },
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true,
                placeholder = {
                    Text(text = stringResource(id = R.string.search_placeholder))
                }
            )
            IconButton(
                onClick = {
                    scanBarcode(barcodeLauncher)
                },
                modifier = Modifier
                    .weight(weightValue)
                    .alignBy(FirstBaseline)
                    .align(alignment = Alignment.CenterVertically),
                enabled = isFrontLayerDisabled != true
            ) {
                Icon(
                    painter = qrcodeResource,
                    contentDescription = "",
                    tint = themeColor
                )
            }
            AnimatedVisibility(
                visible = backdropState.isRevealed
            ) {
                IconButton(
                    onClick = {
                        scope.launch { backdropState.conceal() }
                    },
                    modifier = Modifier
                        .weight(weightValueArrow, weightValueArrowStatus)
                        .alignBy(FirstBaseline)
                        .align(alignment = Alignment.CenterVertically)
                ) {
                    if (isFrontLayerDisabled == true) {
                        Icon(
                            resource,
                            contentDescription = null,
                            tint = themeColor
                        )
                    } else {
                        Icon(
                            resource,
                            contentDescription = null,
                            tint = themeColor
                        )
                    }
                }
            }
            closeButtonVisibility = when (search) {
                "" -> 0f
                else -> 1f
            }
            manageStockViewModel.onSearchQueryChanged(search)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.height(heightIs)
        ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 0.dp)
        ) {
            if (viewModel.toolbarTitle.collectAsState().value.name
                == TransactionType.DISTRIBUTION.name
            ) {
                if (viewModel.hasFacilitySelected.collectAsState().value &&
                    hasDestinationSelected == true
                ) {
                    updateTableState(manageStockViewModel, viewModel)
                    ManageStockTable(manageStockViewModel) {
                        scope.launch { backdropState.conceal() }
                    }
                }
            } else if (viewModel.toolbarTitle.collectAsState().value.name
                == TransactionType.CORRECTION.name
            ) {
                if (viewModel.hasFacilitySelected.collectAsState().value) {
                    updateTableState(manageStockViewModel, viewModel)
                    ManageStockTable(manageStockViewModel) {
                        scope.launch { backdropState.conceal() }
                    }
                }
            } else if (viewModel.toolbarTitle.collectAsState().value.name
                == TransactionType.DISCARD.name
            ) {
                if (viewModel.hasFacilitySelected.collectAsState().value) {
                    updateTableState(manageStockViewModel, viewModel)
                    ManageStockTable(manageStockViewModel) {
                        scope.launch { backdropState.conceal() }
                    }
                }
            }
        }
    }
}

private fun updateTableState(
    manageStockViewModel: ManageStockViewModel,
    viewModel: HomeViewModel
) {
    manageStockViewModel.setup(viewModel.getData())
}

private fun scanBarcode(launcher: ActivityResultLauncher<ScanOptions>) {
    val scanOptions = ScanOptions()
        .setBeepEnabled(true)
        .setCaptureActivity(ScannerActivity::class.java)
    launcher.launch(scanOptions)
}
