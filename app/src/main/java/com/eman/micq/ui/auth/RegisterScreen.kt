package com.eman.micq.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.eman.micq.ui.theme.MicQTheme
import com.eman.micq.viewmodel.AuthState
import com.eman.micq.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    role: String,
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onRegistrationSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()

    RegisterContent(
        role = role,
        authState = authState,
        onNavigateBack = onNavigateBack,
        onRegister = { name, email, password ->
            viewModel.register(name, email, password, role)
        }
    )

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onRegistrationSuccess()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    role: String,
    authState: AuthState,
    onNavigateBack: () -> Unit,
    onRegister: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register as $role") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            Text(
                text = "Join the Team",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { onRegister(name, email, password) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = authState !is AuthState.Loading,
                shape = MaterialTheme.shapes.medium
            ) {
                Text("CREATE ACCOUNT", style = MaterialTheme.typography.titleLarge)
            }

            if (authState is AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    MicQTheme {
        RegisterContent(
            role = "DJ",
            authState = AuthState.Idle,
            onNavigateBack = {},
            onRegister = { _, _, _ -> }
        )
    }
}
