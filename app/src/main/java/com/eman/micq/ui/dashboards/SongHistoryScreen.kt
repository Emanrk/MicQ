package com.eman.micq.ui.dashboards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eman.micq.data.model.QueueEntry
import com.eman.micq.viewmodel.SongHistoryState
import com.eman.micq.viewmodel.SongHistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: SongHistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // The ViewModel init now calls loadHistory() automatically 
    // for the logged-in user.

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Song History") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is SongHistoryState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SongHistoryState.Success -> {
                    SongHistoryList(entries = state.entries)
                }
                is SongHistoryState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun SongHistoryList(entries: List<QueueEntry>) {
    if (entries.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No song history for this session.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(entries) { entry ->
                SongHistoryItem(entry = entry)
            }
        }
    }
}

@Composable
fun SongHistoryItem(entry: QueueEntry) {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val completedTime = if (entry.completedAt != null) dateFormat.format(Date(entry.completedAt)) else "N/A"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = entry.songName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${entry.firstName} ${entry.lastName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                text = completedTime,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
