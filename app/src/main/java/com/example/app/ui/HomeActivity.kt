/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.core.navigation.FeatureRegistry
import com.example.core.ui.EnterpriseTheme

class HomeActivity : ComponentActivity() {

    private val vm: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.load(this)

        setContent {
            EnterpriseTheme(darkTheme = isSystemInDarkTheme()) {
                val state by vm.state.observeAsState(HomeUiState.Loading)

                HomeScreen(
                    state = state,
                    onRowClick = { featureId ->
                        val entry = FeatureRegistry.get(featureId)
                        if (entry == null) {
                            Toast.makeText(
                                this,
                                "Feature not found: $featureId",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@HomeScreen
                        }
                        startActivity(entry.intent(this))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    state: HomeUiState,
    onRowClick: (String) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Home") }) }
    ) { padding ->
        when (state) {
            HomeUiState.Loading -> Text(
                "Loading…",
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            )

            is HomeUiState.Error -> Text(
                "Failed: ${state.message}",
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            )

            is HomeUiState.Content -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.rows) { row ->
                        HomeRowCard(row = row, onClick = { onRowClick(row.featureId) })
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeRowCard(row: HomeRow, onClick: () -> Unit) {
    Card(onClick = onClick,
        modifier = Modifier.fillMaxWidth() ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = row.iconRes),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = row.title)
                Text(text = row.message)
            }
        }
    }
}
