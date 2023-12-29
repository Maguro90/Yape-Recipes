package com.maguro.recipes.presentation.screens.utils

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.maguro.recipes.R
import com.maguro.recipes.data.repository.ErrorType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@Composable
fun ErrorSnackbar(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    error: ErrorType
) {
    if (error !is ErrorType.None) {
        var job: Job? by remember { mutableStateOf(null) }
        val message = stringResource(id = error.messageResourceId)
        SideEffect {
            job = coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = message
                )
            }
        }
    }
}

@Composable
fun EmptyContent(
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit
) {
    MessageContent(
        modifier = modifier,
        stringResource = R.string.message_notice_noInfoFound,
        onRetryClick = onRetryClick
    )
}

@Composable
fun ErrorContent(
    modifier: Modifier = Modifier,
    errorType: ErrorType,
    onRetryClick: () -> Unit
) {
    MessageContent(
        modifier = modifier,
        stringResource = errorType.messageResourceId,
        onRetryClick = onRetryClick
    )
}

@Composable
private fun MessageContent(
    modifier: Modifier = Modifier,
    @StringRes
    stringResource: Int,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = stringResource),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetryClick) {
            Text(text = stringResource(id = R.string.button_text_retry))
        }
    }
}

val ErrorType.messageResourceId: Int
    get() = when (this) {
        is ErrorType.None -> ResourcesCompat.ID_NULL
        is ErrorType.Unknown -> R.string.message_error_unknown
        is ErrorType.Connection -> R.string.message_error_connection
        is ErrorType.Server -> R.string.message_error_server
        is ErrorType.ConnectionTimeout -> R.string.message_error_connectionTimeout
    }