package org.dhis2.android.rtsm.ui.home.screens

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import org.dhis2.android.rtsm.R
import org.dhis2.android.rtsm.ui.home.HomeViewModel
import org.dhis2.android.rtsm.ui.home.screens.components.Toolbar

@Composable
fun HomeScreen(
    activity: Activity,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    themeColor: Color,
    proceedAction: (scope: CoroutineScope, scaffoldState: ScaffoldState) -> Unit = { _, _ -> }
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.arrow_forward),
                        contentDescription = null
                    )
                },
                text = { Text(stringResource(R.string.proceed)) },
                onClick = { proceedAction(scope, scaffoldState) },
                backgroundColor = Color.White,
                contentColor = themeColor,
                shape = RoundedCornerShape(10.dp)
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = it) { data ->
                Snackbar(
                    snackbarData = data,
                    backgroundColor = colorResource(R.color.error)
                )
            }
        }
    ) {
        it.calculateBottomPadding()
        Backdrop(activity, viewModel, themeColor)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Backdrop(
    activity: Activity,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    themeColor: Color
) {
    val backdropState = rememberBackdropScaffoldState(BackdropValue.Concealed)

    BackdropScaffold(
        appBar = {
            Toolbar(
                viewModel.toolbarTitle.collectAsState().value.name,
                viewModel.toolbarSubtitle.collectAsState().value,
                themeColor,
                navigationAction = {
                    activity.finish()
                },
                backdropState
            )
        },
        backLayerBackgroundColor = themeColor,
        backLayerContent = {
            FilterList(viewModel, themeColor)
        },
        frontLayerElevation = 5.dp,
        frontLayerContent = {
            Column(Modifier.padding(16.dp)) {
                Text(text = "Main content")
            }
        },
        scaffoldState = backdropState,
        gesturesEnabled = false
    )
}
