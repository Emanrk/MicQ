package com.eman.micq.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.MicExternalOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eman.micq.ui.theme.MicQTheme

data class RoleOption(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val roleKey: String
)

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (String) -> Unit
) {
    RoleSelectionContent(onRoleSelected = onRoleSelected)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionContent(
    onRoleSelected: (String) -> Unit
) {
    val roles = listOf(
        RoleOption(
            title = "Admin",
            description = "Manage staff, sessions, and view performance logs.",
            icon = Icons.Default.Settings,
            roleKey = "ADMIN"
        ),
        RoleOption(
            title = "DJ",
            description = "Clock in for shifts and manage the live performance queue.",
            icon = Icons.Default.MicExternalOn,
            roleKey = "DJ"
        ),
        RoleOption(
            title = "Performer",
            description = "Assign customers to the queue and manage table requests.",
            icon = Icons.Default.Badge,
            roleKey = "PERFORMER"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Your Role") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MicQ Team",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Select your position to customize your experience",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(roles) { role ->
                    RoleCard(role = role) {
                        onRoleSelected(role.roleKey)
                    }
                }
            }
        }
    }
}

@Composable
fun RoleCard(
    role: RoleOption,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = role.icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column {
                Text(
                    text = role.title,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = role.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectionScreenPreview() {
    MicQTheme {
        RoleSelectionContent(onRoleSelected = {})
    }
}
