package com.eman.micq.ui.dj

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.eman.micq.ui.theme.MicQTheme
import com.eman.micq.viewmodel.DjUiState
import com.eman.micq.viewmodel.DjViewModel

@Composable
fun DjShiftScreen(
    viewModel: DjViewModel,
    onNavigateToDashboard: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    DjShiftContent(
        uiState = uiState,
        onStartShift = { viewModel.startShift() },
        onEndShift = { viewModel.endShift() },
        onNavigateToDashboard = onNavigateToDashboard,
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DjShiftContent(
    uiState: DjUiState,
    onStartShift: () -> Unit,
    onEndShift: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shift Management") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState.activeShift == null) {
                Text(
                    text = "MicQ Ready",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Sign on to start your session",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )
                Button(
                    onClick = onStartShift,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("START SHIFT", style = MaterialTheme.typography.titleLarge)
                }
            } else {
                Text(
                    text = "SHIFT ACTIVE",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = uiState.elapsedTime,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 64.sp
                    ),
                    modifier = Modifier.padding(vertical = 24.dp)
                )
                
                Button(
                    onClick = onNavigateToDashboard,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("OPEN QUEUE CONTROL", style = MaterialTheme.typography.titleLarge)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = onEndShift,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("END SHIFT", style = MaterialTheme.typography.titleLarge)
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DjShiftScreenActivePreview() {
    MicQTheme {
        DjShiftContent(
            uiState = DjUiState(
                activeShift = com.eman.micq.data.model.DjShift(
                    id = "1",
                    djId = "dj1",
                    djName = "DJ Mike",
                    startTime = System.currentTimeMillis()
                ),
                elapsedTime = "01:22:45"
            ),
            onStartShift = {},
            onEndShift = {},
            onNavigateToDashboard = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DjShiftScreenIdlePreview() {
    MicQTheme {
        DjShiftContent(
            uiState = DjUiState(activeShift = null),
            onStartShift = {},
            onEndShift = {},
            onNavigateToDashboard = {},
            onLogout = {}
        )
    }
}
