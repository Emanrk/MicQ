


package com.eman.micq.ui.dashboards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eman.micq.data.model.DjShift
import com.eman.micq.data.repository.SingerLoyalty
import com.eman.micq.viewmodel.AdminActivityState
import com.eman.micq.viewmodel.AdminActivityViewModel
import com.eman.micq.viewmodel.ShiftActivity
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.ui.tooling.preview.Preview
import com.eman.micq.ui.theme.MicQTheme

@Composable
fun AdminDashboardScreen(
    onNavigateToShiftHistory: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AdminActivityViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AdminDashboardContainer(
        uiState = uiState,
        onRefresh = { viewModel.loadActivity() },
        onNavigateToShiftHistory = onNavigateToShiftHistory,
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContainer(
    uiState: AdminActivityState,
    onRefresh: () -> Unit,
    onNavigateToShiftHistory: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Console") },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onNavigateToShiftHistory) {
                        Icon(Icons.Default.History, contentDescription = "Full History")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is AdminActivityState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is AdminActivityState.Success -> {
                    AdminDashboardContent(
                        activities = state.shiftActivities,
                        loyaltyData = state.loyaltyData
                    )
                }
                is AdminActivityState.Error -> {
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
fun AdminDashboardContent(
    activities: List<ShiftActivity>,
    loyaltyData: List<SingerLoyalty>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Live & Recent Shifts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (activities.isEmpty()) {
            item {
                EmptyStateMotif("No recent DJ activity found.")
            }
        } else {
            items(activities) { activity ->
                DjActivityCard(activity = activity)
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Regular Karaoke Fans",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (loyaltyData.isEmpty()) {
            item {
                EmptyStateMotif("No loyalty data available yet.")
            }
        } else {
            items(loyaltyData) { loyalty ->
                LoyaltyCard(loyalty = loyalty)
            }
        }
    }
}

@Composable
fun EmptyStateMotif(text: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun LoyaltyCard(loyalty: SingerLoyalty) {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val lastSeen = dateFormat.format(Date(loyalty.lastVisitTimestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val displayName = if (loyalty.preferredName.isNotBlank()) {
                    "${loyalty.preferredName} (${loyalty.firstName})"
                } else {
                    "${loyalty.firstName} ${loyalty.lastName}"
                }
                Text(text = displayName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Last seen: $lastSeen",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = loyalty.visitCount.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Visits",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun DjActivityCard(activity: ShiftActivity) {
    val shift = activity.shift
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val lastActive = dateFormat.format(Date(shift.lastActiveAt))
    
    // Check if DJ has been quiet for more than 5 minutes
    val isQuiet = System.currentTimeMillis() - shift.lastActiveAt > 5 * 60 * 1000
    val statusColor = if (shift.endTime != null) {
        MaterialTheme.colorScheme.outline
    } else if (isQuiet) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    var showEvents by remember { mutableStateOf(false) }

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
                Text(text = shift.djName, style = MaterialTheme.typography.titleLarge)
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = if (shift.endTime != null) "Finished" else if (isQuiet) "QUIET" else "ACTIVE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Songs Completed:", style = MaterialTheme.typography.bodyMedium)
                Text(activity.songCount.toString(), fontWeight = FontWeight.Bold)
            }
            
            Text(
                text = "Last Active: $lastActive",
                style = MaterialTheme.typography.bodySmall,
                color = if (isQuiet && shift.endTime == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (shift.events.isNotEmpty()) {
                TextButton(
                    onClick = { showEvents = !showEvents },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (showEvents) "Hide Connection Logs" else "Show Connection Logs (${shift.events.size})")
                }

                if (showEvents) {
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    shift.events.values.sortedByDescending { it.timestamp }.take(5).forEach { event ->
                        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(event.timestamp))
                        Text(
                            text = "[$time] ${event.type}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (event.type == "DISCONNECT") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardScreenPreview() {
    val fakeActivities = listOf(
        ShiftActivity(
            shift = DjShift(
                id = "1",
                djId = "dj1",
                djName = "DJ Mike",
                startTime = System.currentTimeMillis() - 3600000,
                lastActiveAt = System.currentTimeMillis() - 60000
            ),
            songCount = 12
        ),
        ShiftActivity(
            shift = DjShift(
                id = "2",
                djId = "dj2",
                djName = "DJ Sarah",
                startTime = System.currentTimeMillis() - 7200000,
                endTime = System.currentTimeMillis() - 3600000,
                lastActiveAt = System.currentTimeMillis() - 3600000
            ),
            songCount = 25
        )
    )

    val fakeLoyalty = listOf(
        SingerLoyalty(
            firstName = "John",
            lastName = "Doe",
            preferredName = "Johnny Karaoke",
            visitCount = 15,
            lastVisitTimestamp = System.currentTimeMillis() - 86400000
        ),
        SingerLoyalty(
            firstName = "Alice",
            lastName = "Smith",
            preferredName = "",
            visitCount = 8,
            lastVisitTimestamp = System.currentTimeMillis() - 172800000
        )
    )

    MicQTheme {
        AdminDashboardContainer(
            uiState = AdminActivityState.Success(fakeActivities, fakeLoyalty),
            onRefresh = {},
            onNavigateToShiftHistory = {},
            onLogout = {}
        )
    }
}
