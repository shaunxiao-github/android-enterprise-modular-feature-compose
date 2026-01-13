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
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.core.ui.EnterpriseTheme
import com.example.feature.messagecentre.impl.domain.Message
import com.example.feature.messagecentre.impl.ui.*
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.ExperimentalMaterial3Api


class MessageCentreDetailActivity : ComponentActivity() {

    private lateinit var vm: McDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra(MessageCentreListActivity.EXTRA_ID).orEmpty()
        val repo = MessageCentreGraph.repository(this)
        vm = ViewModelProvider(this, McDetailVmFactory(repo))[McDetailViewModel::class.java]
        vm.start(id)

        setContent {
            EnterpriseTheme(darkTheme = isSystemInDarkTheme()) {
                val msg by vm.message.observeAsState(null)
                val err by vm.error.observeAsState(null)

                MessageDetailScreen(
                    message = msg,
                    error = err,
                    onBack = { finish() },
                    onDelete = {
                        vm.delete(id) { finish() }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageDetailScreen(
    message: Message?,
    error: String?,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mc_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (!error.isNullOrBlank()) {
                Text(text = "Error: $error", modifier = Modifier.padding(bottom = 12.dp))
            }

            if (message == null) {
                Text("Message not found (maybe deleted).")
                return@Column
            }

            Text(text = message.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                text = formatTime(message.timestampMillis),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(16.dp))
            Text(text = message.content, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onDelete) { Text(stringResource(R.string.mc_delete)) }
        }
    }
}