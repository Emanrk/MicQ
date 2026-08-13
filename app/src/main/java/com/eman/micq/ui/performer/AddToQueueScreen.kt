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
        onAddEntry = { firstName, lastName, songName, tableNumber ->
            viewModel.addEntry(
                sessionId = sessionId,
                firstName = firstName,
                lastName = lastName,
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
    onAddEntry: (String, String, String, String) -> Unit
) {
    var firstName by remember { mutableStateOf(initialFirstName) }
    var lastName by remember { mutableStateOf("") }
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
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Customer First Name *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Customer Last Name (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = songName,
                onValueChange = { songName = it },
                label = { Text("Song Name *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tableNumber,
                onValueChange = { tableNumber = it },
                label = { Text("Table Number (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onAddEntry(firstName, lastName, songName, tableNumber)
                    // Reset form fields after calling the add entry lambda
                    // Note: In the real screen, onSuccess handles clearing if needed, 
                    // but for simpler previews we can just clear here or let the parent handle it.
                    // To match user's request "clear all form fields" on success:
                    firstName = ""
                    lastName = ""
                    songName = ""
                    tableNumber = ""
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isButtonEnabled
            ) {
                Text("Add to Queue")
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
            onAddEntry = { _, _, _, _ -> }
        )
    }
}
