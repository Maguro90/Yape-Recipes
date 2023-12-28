package com.maguro.recipes.presentation.screens.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshBox(
    modifier: Modifier = Modifier,
    state: PullToRefreshState,
    onRefresh: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .nestedScroll(state.nestedScrollConnection)
    ) {

        content()

        onRefresh(
            pullToRefreshState = state,
            block = onRefresh
        )

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = state
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun onRefresh(pullToRefreshState: PullToRefreshState, block: () -> Unit) {
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(null) {
            block()
        }
    }
}
