package org.dhis2.android.rtsm.ui.home.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.dhis2.android.rtsm.R
import timber.log.Timber


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainContent(
    backdropState: BackdropScaffoldState,
    isFrontLayerDisabled: Boolean?
) {
    val scope = rememberCoroutineScope()
    val resource = painterResource(R.drawable.ic_arrow_up)
    val qrcodeResource = painterResource(R.drawable.ic_qr_code_scanner)
    val searchResource = painterResource(R.drawable.ic_search)
    val closeResource = painterResource(R.drawable.ic_close)
    val commentsAlpha = if (backdropState.isRevealed) 1f else 0f
    var closeButtonVisibility by remember { mutableStateOf(0f) }
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        var search by remember { mutableStateOf("") }
        OutlinedTextField(
            value = search,
            onValueChange = {
                search = it
                closeButtonVisibility = when (search) {
                    "" -> 0f
                    else -> 1f
                }
            },
            modifier = Modifier
                .background(Color.White, shape = CircleShape)
                .padding(8.dp)
                .shadow(
                    elevation = 8.dp,
                    ambientColor = Color.Black.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(30.dp),
                    clip = false
                )
                .offset(0.dp, 0.dp)
                .background(color = Color.White, shape = RoundedCornerShape(30.dp))
                .weight(3f),
            shape = RoundedCornerShape(30.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
            leadingIcon = {
                Icon(
                    painter = searchResource,
                    contentDescription = "",
                    tint = colorResource(id = R.color.primary_stock)
                )
            },
            trailingIcon = {
                IconButton(
                    modifier = Modifier
                        .alpha(closeButtonVisibility),
                    onClick = {
                        search = ""
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
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    Timber.tag("SEARCH_DATA").v(search)
                }
            )
        )
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        ) {
            Icon(
                painter = qrcodeResource,
                contentDescription = "",
                tint = colorResource(id = R.color.primary_stock)
            )
        }
        IconButton(
            onClick = {
                scope.launch { backdropState.conceal() }
            },
            modifier = Modifier
                .alpha(commentsAlpha)
                .padding(8.dp)
                .weight(1f)
        ) {
            if (isFrontLayerDisabled == true) {
                Icon(
                    resource,
                    contentDescription = null,
                    tint = colorResource(id = R.color.primary_stock)
                )
            } else {
                Icon(
                    resource,
                    contentDescription = null,
                    tint = colorResource(id = R.color.primary_stock)
                )
            }
        }
    }
}
