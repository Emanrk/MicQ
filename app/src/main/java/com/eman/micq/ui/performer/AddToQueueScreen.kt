package com.eman.micq.ui.performer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eman.micq.ui.theme.MicQTheme
import com.eman.micq.viewmodel.QueueViewModel
import kotlinx.coroutines.launch

@Composable
fun AddToQueueScreen(
    sessionId: String,
    viewModel: QueueViewModel,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    AddToQueueContent(
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        onAddEntry = { firstName, lastName, preferredName, songName, tableNumber ->
            viewModel.addEntry(
                sessionId = sessionId,
                firstName = firstName,
                lastName = lastName,
                preferredName = preferredName,
                songName = songName,
                tableNumber = tableNumber,
                onSuccess = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Added to queue!")
                    }
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToQueueContent(
    initialFirstName: String = "",
    initialSongName: String = "",
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onAddEntry: (String, String, String, String, String) -> Unit
) {
    var firstName by remember { mutableStateOf(initialFirstName) }
    var lastName by remember { mutableStateOf("") }
    var preferredName by remember { mutableStateOf("") }
    var songName by remember { mutableStateOf(initialSongName) }
    var tableNumber by remember { mutableStateOf("") }

    val isButtonEnabled = firstName.isNotBlank() && songName.isNotBlank()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Add to Queue") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Singer Information",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name *") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = preferredName,
                onValueChange = { preferredName = it },
                label = { Text("Preferred / Stage Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                placeholder = { Text("e.g. The Rockstar") }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Performance Details",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = songName,
                onValueChange = { songName = it },
                label = { Text("Song Title *") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = tableNumber,
                onValueChange = { tableNumber = it },
                label = { Text("Table / Seat Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onAddEntry(firstName, lastName, preferredName, songName, tableNumber)
                    firstName = ""
                    lastName = ""
                    preferredName = ""
                    songName = ""
                    tableNumber = ""
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = isButtonEnabled,
                shape = MaterialTheme.shapes.medium
            ) {
                Text("ADD TO QUEUE", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddToQueueScreenPreview() {
    MicQTheme {
        AddToQueueContent(
            initialFirstName = "Sarah",
            initialSongName = "Wonderwall",
            onNavigateBack = {},
            snackbarHostState = remember { SnackbarHostState() },
            onAddEntry = { _, _, _, _, _ -> }
        )
    }
}
