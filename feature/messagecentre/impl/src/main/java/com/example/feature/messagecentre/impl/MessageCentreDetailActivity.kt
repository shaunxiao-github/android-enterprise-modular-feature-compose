/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.feature.messagecentre.impl


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.core.ui.EnterpriseTheme
import com.example.feature.messagecentre.impl.ui.McDetailViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.feature.messagecentre.impl.ui.*


class MessageCentreDetailActivity : ComponentActivity() {

    companion object {
        const val EXTRA_MESSAGE_ID = "message_id"
    }

    // Simple factory; you might wire repo from a feature graph / ServiceLocator
    @Suppress("UNCHECKED_CAST")
    private val vm: McDetailViewModel by lazy {
        val repo = MessageCentreGraph.repository(this)// your DI helper
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return McDetailViewModel(repo) as T
            }
        })[McDetailViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra(EXTRA_MESSAGE_ID)
        if (id == null) {
            finish()
            return
        }

        vm.start(id)

        setContent {
            EnterpriseTheme(darkTheme = isSystemInDarkTheme()) {
                val state by vm.state.collectAsState()
                val transientError by vm.transientError.collectAsState()

                MessageDetailScreen(
                    state = state,
                    transientError = transientError,
                    onBack = { finish() },
                    onDelete = { vm.delete(onDeleted = { finish() }) },
                    onTransientErrorConsumed = { vm.clearTransientError() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailScreen(
    state: McDetailState,
    transientError: String?,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onTransientErrorConsumed: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(transientError) {
        if (!transientError.isNullOrBlank()) {
            snackbarHostState.showSnackbar(transientError)
            onTransientErrorConsumed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mc_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (state) {
                McDetailState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is McDetailState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                is McDetailState.Content -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = state.title,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = formatTime(state.timestampMillis),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = state.content,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.height(24.dp))

                        Button(onClick = onDelete) {
                            Text(stringResource(R.string.mc_delete))
                        }
                    }
                }
            }
        }
    }
}
