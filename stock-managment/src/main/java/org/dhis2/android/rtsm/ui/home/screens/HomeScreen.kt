package org.dhis2.android.rtsm.ui.home.screens

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import org.dhis2.android.rtsm.R
import org.dhis2.android.rtsm.ui.home.HomeActivity
import org.dhis2.android.rtsm.ui.home.HomeViewModel
import org.dhis2.android.rtsm.ui.home.screens.components.Backdrop
import org.dhis2.android.rtsm.ui.managestock.ManageStockViewModel

@Composable
fun HomeScreen(
    activity: Activity,
    viewModel: HomeViewModel = viewModel(),
    manageStockViewModel: ManageStockViewModel = viewModel(),
    themeColor: Color,
    supportFragmentManager: FragmentManager,
    homeContext: HomeActivity,
    barcodeLauncher: ActivityResultLauncher<ScanOptions>,
    proceedAction: (scope: CoroutineScope, scaffoldState: ScaffoldState) -> Unit = { _, _ -> },
    syncAction: (scope: CoroutineScope, scaffoldState: ScaffoldState) -> Unit = { _, _ -> }
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val purple200 = Color(0xFF757575)
    val purple2000 = Color(0xFF616161)
    var enabled by remember { mutableStateOf(true) }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {

            CompositionLocalProvider(
                LocalRippleTheme provides
                    if (enabled) LocalRippleTheme.current else NoRippleTheme
            ) {

                ExtendedFloatingActionButton(
                    modifier = Modifier.height(60.dp),
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.arrow_forward),
                            contentDescription = stringResource(R.string.proceed)
                        )
                    },
                    text = { Text(stringResource(R.string.proceed)) },
                    onClick = {
                        enabled != enabled
                        if (enabled) {
                            proceedAction(scope, scaffoldState)
                        }
                    },
//                    backgroundColor = if (enabled) MaterialTheme.colors.secondary else Gray,
//                    backgroundColor = purple200,
//                backgroundColor = Color.Unspecified,
                contentColor = purple2000,
//                contentColor = if (enabled) LocalContentColor.current.copy(alpha = LocalContentAlpha.current) else DarkGray,
//                contentColor = themeColor,
//                contentColor = colorResource(id = R.color.button_proceed_text_color),
//                shape = RoundedCornerShape(10.dp)
                )

//                FloatingActionButton(
//                    backgroundColor = if (enabled) MaterialTheme.colors.secondary else Gray,
//                    onClick = { if (enabled) { /* do something */ } else {} },
//                ) {
//                    Icon(Icons.Filled.Favorite,
//                        contentDescription = "Localized description",
//                        tint = if (enabled)
//                            LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
//                        else DarkGray)
//                }
            }


//            ExtendedFloatingActionButton(
//                modifier = Modifier.height(60.dp),
//                icon = {
//                    Icon(
//                        painter = painterResource(R.drawable.arrow_forward),
//                        contentDescription = stringResource(R.string.proceed)
//                    )
//                },
//                text = { Text(stringResource(R.string.proceed)) },
//                onClick = { proceedAction(scope, scaffoldState) },
//                backgroundColor = purple200,
////                backgroundColor = Color.Unspecified,
////                contentColor = Color.LightGray,
////                contentColor = themeColor,
////                contentColor = colorResource(id = R.color.button_proceed_text_color),
////                shape = RoundedCornerShape(10.dp)
//            )
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
        Backdrop(
            activity,
            viewModel,
            manageStockViewModel,
            themeColor,
            supportFragmentManager,
            homeContext,
            barcodeLauncher,
            scaffoldState
        ) { coroutineScope, scaffold ->
            syncAction(coroutineScope, scaffold)
        }
    }

}

private object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f, 0.0f, 0.0f, 0.0f)
}
