package com.yuricunha.yumusic.ui.screens.radio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.data.api.InternetRadioStationDto
import com.yuricunha.yumusic.ui.theme.*
import com.yuricunha.yumusic.util.ScreenState

data class CustomStation(val name: String, val streamUrl: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioTabScreen(
    onPlayStation: (String, String) -> Unit, // name, url
    modifier: Modifier = Modifier,
    viewModel: RadioTabViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val customStations by viewModel.customStations.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.nav_radio), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Normal)) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Background),
        )

        LazyColumn(Modifier.fillMaxSize()) {
            // Add custom station button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { showAddDialog = true }.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(40.dp).padding(8.dp)) { Icon(Icons.Filled.Add, null, tint = PrimaryAccent) }
                    Spacer(Modifier.width(12.dp))
                    Text("Add custom station", style = MaterialTheme.typography.bodyLarge, color = PrimaryAccent)
                }
                HorizontalDivider(color = Divider, thickness = 0.5.dp)
            }

            // Custom stations
            if (customStations.isNotEmpty()) {
                item {
                    Text("My Stations", style = MaterialTheme.typography.titleSmall, color = TextPrimary, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
                }
                customStations.forEach { station ->
                    item {
                        StationRow(station.name, station.streamUrl, onClick = { onPlayStation(station.name, station.streamUrl) })
                    }
                }
                item { HorizontalDivider(color = Divider, thickness = 0.5.dp) }
            }

            // API stations
            when (uiState) {
                is ScreenState.Loading -> item { Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryAccent) } }
                is ScreenState.Error -> item { }
                is ScreenState.Success -> {
                    @Suppress("UNCHECKED_CAST")
                    val stations = (uiState as ScreenState.Success).data as? List<InternetRadioStationDto>
                    if (!stations.isNullOrEmpty()) {
                        item {
                            Text("Server Stations", style = MaterialTheme.typography.titleSmall, color = TextPrimary, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
                        }
                        items(stations) { s ->
                            StationRow(s.name, s.streamUrl, onClick = { onPlayStation(s.name, s.streamUrl) })
                        }
                    }
                }
            }
        }
    }

    // Add station dialog
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var url by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Radio Station") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Station name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Stream URL") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank() && url.isNotBlank()) {
                        viewModel.addCustomStation(name, url)
                        showAddDialog = false
                    }
                }) { Text("Add", color = PrimaryAccent) }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } },
            containerColor = BackgroundElevated,
        )
    }
}

@Composable
private fun StationRow(name: String, url: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.Radio, null, tint = PrimaryAccent, modifier = Modifier.size(32.dp))
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(url, style = MaterialTheme.typography.labelSmall, color = TextTertiary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Icon(Icons.Filled.PlayArrow, null, tint = TextSecondary, modifier = Modifier.size(24.dp))
    }
    HorizontalDivider(color = Divider, thickness = 0.5.dp, modifier = Modifier.padding(start = 66.dp))
}