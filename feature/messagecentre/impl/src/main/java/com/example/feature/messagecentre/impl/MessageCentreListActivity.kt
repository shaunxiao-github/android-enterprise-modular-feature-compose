/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */
 
package com.example.feature.messagecentre.impl

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.livedata.observeAsState
import com.example.core.ui.EnterpriseTheme
import com.example.feature.messagecentre.impl.ui.*
import androidx.compose.ui.res.stringResource

class MessageCentreListActivity : ComponentActivity() {

    private lateinit var vm: McListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = MessageCentreGraph.repository(this)
        vm = ViewModelProvider(this, McListVmFactory(repo))[McListViewModel::class.java]
        vm.start()

        setContent {
            EnterpriseTheme(darkTheme = isSystemInDarkTheme()) {
                val state by vm.state.observeAsState(McListState.Loading)
                val err by vm.transientError.observeAsState(null)

                LaunchedEffect(err) {
                    // no-op; handled by snackbar host
                }

                MessageListScreen(
                    state = state,
                    transientError = err,
                    onRefresh = { vm.refresh() },
                    onBack = { finish() },
                    onClick = { id ->
                        startActivity(
                            Intent(
                                this,
                                MessageCentreDetailActivity::class.java
                            ).putExtra(EXTRA_ID, id)
                        )
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_ID = "message_id"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageListScreen(
    state: McListState,
    transientError: String?,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    onClick: (String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(transientError) {
        if (!transientError.isNullOrBlank()) {
            snackbarHostState.showSnackbar(transientError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mc_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.mc_refresh)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(Modifier
            .padding(padding)
            .fillMaxSize()) {
            when (state) {
                McListState.Loading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is McListState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                is McListState.Content -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.items) { item ->
                            MessageRow(item = item, onClick = { onClick(item.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageRow(item: MessageListItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                text = formatTime(item.timestampMillis),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}