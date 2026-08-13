package com.eman.micq.ui.dj

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eman.micq.data.model.QueueEntry
import com.eman.micq.ui.theme.MicQTheme
import com.eman.micq.viewmodel.QueueUiState
import com.eman.micq.viewmodel.QueueViewModel

@Composable
fun DjDashboardScreen(
    sessionId: String,
    viewModel: QueueViewModel,
    onSignOff: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(sessionId) {
        viewModel.observeQueue(sessionId)
    }

    DjDashboardContent(
        uiState = uiState,
        onSignOff = onSignOff,
        onLogout = onLogout,
        onMarkSinging = { entryId -> viewModel.updateEntryStatus(sessionId, entryId, "SINGING") },
        onMarkDone = { entryId -> viewModel.updateEntryStatus(sessionId, entryId, "DONE") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DjDashboardContent(
    uiState: QueueUiState,
    onSignOff: () -> Unit,
    onLogout: () -> Unit,
    onMarkSinging: (String) -> Unit,
    onMarkDone: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Queue Control") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = onSignOff,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("END SHIFT & SIGN OFF")
                }
            }
        }
    ) { padding ->
        if (uiState.entries.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No singers in queue yet", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(uiState.entries) { index, entry ->
                    QueueItemCard(
                        position = index + 1,
                        entry = entry,
                        onMarkSinging = { onMarkSinging(entry.id) },
                        onMarkDone = { onMarkDone(entry.id) }
                    )
                }
            }
        }

        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(padding))
        }
    }
}

@Composable
fun QueueItemCard(
    position: Int,
    entry: QueueEntry,
    onMarkSinging: () -> Unit,
    onMarkDone: () -> Unit
) {
    val statusColor = when (entry.status) {
        "SINGING" -> Color(0xFF4CAF50) // Green
        "DONE" -> Color(0xFF2196F3)    // Blue
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#$position",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = entry.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${entry.firstName} ${entry.lastName}",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = entry.songName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            if (entry.tableNumber.isNotBlank()) {
                Text(
                    text = "Table: ${entry.tableNumber}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (entry.status != "SINGING" && entry.status != "DONE") {
                    Button(
                        onClick = onMarkSinging,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Singing")
                    }
                }
                
                if (entry.status != "DONE") {
                    OutlinedButton(
                        onClick = onMarkDone,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DjDashboardScreenPreview() {
    val fakeEntries = listOf(
        QueueEntry(
            id = "1",
            firstName = "Sarah",
            lastName = "",
            songName = "I Will Always Love You",
            tableNumber = "3",
            status = "WAITING"
        ),
        QueueEntry(
            id = "2",
            firstName = "John",
            lastName = "",
            songName = "Wonderwall",
            tableNumber = "5",
            status = "SINGING"
        )
    )
    
    MicQTheme {
        DjDashboardContent(
            uiState = QueueUiState(entries = fakeEntries),
            onSignOff = {},
            onLogout = {},
            onMarkSinging = {},
            onMarkDone = {}
        )
    }
}
