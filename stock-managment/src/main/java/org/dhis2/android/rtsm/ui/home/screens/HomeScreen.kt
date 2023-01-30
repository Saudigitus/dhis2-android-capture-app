package org.dhis2.android.rtsm.ui.home.screens

import android.app.Activity
import android.graphics.Color.parseColor
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import org.dhis2.android.rtsm.R
import org.dhis2.android.rtsm.ui.home.HomeViewModel
import org.dhis2.android.rtsm.ui.home.screens.components.Backdrop
import org.dhis2.android.rtsm.ui.managestock.ManageStockViewModel
import org.dhis2.commons.resources.ColorUtils
import org.dhis2.ui.buttons.FAButton

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    activity: Activity,
    viewModel: HomeViewModel = viewModel(),
    manageStockViewModel: ManageStockViewModel = viewModel(),
    themeColor: Color,
    supportFragmentManager: FragmentManager,
    barcodeLauncher: ActivityResultLauncher<ScanOptions>,
    proceedAction: (scope: CoroutineScope, scaffoldState: ScaffoldState) -> Unit = { _, _ -> },
    syncAction: (scope: CoroutineScope, scaffoldState: ScaffoldState) -> Unit = { _, _ -> }
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val dataEntryUiState by manageStockViewModel.dataEntryUiState.collectAsState()

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            AnimatedVisibility(
                visible = dataEntryUiState.button.visible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FAButton(
                    text = dataEntryUiState.button.text,
                    contentColor = dataEntryUiState.button.contentColor,
                    containerColor = dataEntryUiState.button.containerColor,
                    enabled = dataEntryUiState.button.visible,
                    icon = {
                        Icon(
                            painter = painterResource(id = dataEntryUiState.button.icon),
                            contentDescription = stringResource(dataEntryUiState.button.text),
                            tint = dataEntryUiState.button.contentColor
                        )
                    }
                ) {
                    proceedAction(scope, scaffoldState)
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = it) {
                ConstraintLayout(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxSize()
                ) {
                    val (snackbar) = createRefs()
                    val painterResource = painterResource(id = dataEntryUiState.snackBarUiState.icon)
                    Snackbar(
                        backgroundColor = Color(colorResource(id = dataEntryUiState.snackBarUiState.color).toArgb()),
                        content = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource, contentDescription = "",
                                    modifier = Modifier.padding(end = (11.23).dp)
                                )
                                Text(
                                    text = stringResource(id = dataEntryUiState.snackBarUiState.message)
                                )
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .height(56.dp)
                            .background(
                                shape = MaterialTheme.shapes.medium.copy(CornerSize(8.dp)),
                                color = Color(0xFF4CAF50)
                            )
                            .constrainAs(snackbar) {
                                bottom.linkTo(parent.bottom, margin = 16.dp)
                                start.linkTo(parent.start, margin = 16.dp)
                                end.linkTo(parent.end, margin = 16.dp)
                            },
                        action = {
                        }
                    )
                }
            }
        }
    ) {
        it.calculateBottomPadding()
        Backdrop(
            activity,
            viewModel,
            manageStockViewModel,
            themeColor,
            supportFragmentManager,
            barcodeLauncher,
            scaffoldState
        ) { coroutineScope, scaffold ->
            syncAction(coroutineScope, scaffold)
        }
    }
}