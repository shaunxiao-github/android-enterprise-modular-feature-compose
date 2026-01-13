/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.feature.payments.impl

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import com.example.core.navigation.FeatureRegistry
import com.example.core.ui.EnterpriseTheme
import com.example.core.ui.components.EnterpriseButton
import com.example.feature.messagecentre.api.MessageCentreFeature
import androidx.compose.ui.unit.dp

class PaymentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            EnterpriseTheme(darkTheme = isSystemInDarkTheme()) {
                FeatureScreen(title = "Payments", onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeatureScreen(title: String, onBack: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("This screen lives inside a feature module (decoupled).")

                Spacer(modifier = Modifier.height(16.dp))

                EnterpriseButton(
                    text = "Open Message Centre",
                    onClick = { openMessageCentre(context) }
                )
            }
        }
    }
}

private fun openMessageCentre(context: Context) {
    val entry = FeatureRegistry.get(MessageCentreFeature.ID)
    if (entry != null) {
        context.startActivity(entry.intent(context))
    } else {
        // In a real app: show a toast/snackbar or log
    }
}
