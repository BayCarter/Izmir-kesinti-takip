package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.OutageItem
import com.example.model.OutageType
import com.example.ui.components.DistrictSelectorDialog
import com.example.ui.components.OutageCard
import com.example.ui.components.OutageDetailDialog
import com.example.ui.components.QuickStatusWidgetCard
import com.example.ui.theme.GedizAmber
import com.example.ui.theme.GedizAmberContainer
import com.example.ui.theme.IzmirBlue
import com.example.ui.theme.IzmirBlueContainer
import com.example.ui.theme.IzmirNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenContainer
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allOutages by viewModel.allOutages.collectAsStateWithLifecycle()
    val filteredOutages by viewModel.filteredOutages.collectAsStateWithLifecycle()
    val notificationLogs by viewModel.notificationLogs.collectAsStateWithLifecycle()
    val unreadCount = remember(notificationLogs) { notificationLogs.count { !it.isRead } }

    var showSearchRow by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Section & Top Bar with Izmir Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Banner Image or Gradient
                Image(
                    painter = painterResource(id = R.drawable.izmir_city_banner_1786698713796),
                    contentDescription = "İzmir Saat Kulesi",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.5f),
                                    IzmirNavy.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // Top Bar Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "İzmir Kesinti Takip",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = IzmirBlue
                                ) {
                                    Text(
                                        text = "CANLI",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "İZSU & Gediz Elektrik Duyuruları",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                        }

                        // Right actions (Search toggle & Notifications)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { showSearchRow = !showSearchRow },
                                modifier = Modifier.testTag("home_search_toggle_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Ara",
                                    tint = Color.White
                                )
                            }

                            IconButton(
                                onClick = { viewModel.setScreen(AppScreen.SETTINGS) },
                                modifier = Modifier.testTag("home_notifications_button")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (unreadCount > 0) {
                                            Badge { Text(unreadCount.toString()) }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Bildirimler",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Location Pill on Hero
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.setShowDistrictPicker(true) }
                            .testTag("home_location_selector_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = uiState.selectedDistrictFilter,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "• Değiştir",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Live Status Widget Card
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                QuickStatusWidgetCard(
                    district = uiState.statusWidgetDistrict,
                    neighborhood = uiState.statusWidgetNeighborhood,
                    allOutages = allOutages,
                    onSelectLocationClick = { viewModel.setShowDistrictPicker(true) }
                )
            }
        }

        // Search bar (collapsible)
        item {
            AnimatedVisibility(visible = showSearchRow) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_input"),
                        placeholder = { Text("İlçe, mahalle veya sokak ara...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }

        // Sync & Refresh Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Aktif Kesintiler",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${filteredOutages.size}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                // Sync button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.refreshData() }
                        .testTag("home_refresh_data_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (uiState.isRefreshing) "Kontrol Ediliyor..." else "Yenile",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        // Filter Chips Row
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All Types Chip
                item {
                    FilterChip(
                        selected = uiState.selectedTypeFilter == "ALL",
                        onClick = { viewModel.setTypeFilter("ALL") },
                        label = { Text("Tüm Kurumlar") },
                        modifier = Modifier.testTag("filter_all_types")
                    )
                }

                // Water (İZSU) Chip
                item {
                    FilterChip(
                        selected = uiState.selectedTypeFilter == "WATER",
                        onClick = {
                            viewModel.setTypeFilter(
                                if (uiState.selectedTypeFilter == "WATER") "ALL" else "WATER"
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = IzmirBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = { Text("💧 İZSU (Su)") },
                        modifier = Modifier.testTag("filter_water")
                    )
                }

                // Electricity (Gediz) Chip
                item {
                    FilterChip(
                        selected = uiState.selectedTypeFilter == "ELECTRICITY",
                        onClick = {
                            viewModel.setTypeFilter(
                                if (uiState.selectedTypeFilter == "ELECTRICITY") "ALL" else "ELECTRICITY"
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = GedizAmber,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = { Text("⚡ Gediz (Elektrik)") },
                        modifier = Modifier.testTag("filter_electricity")
                    )
                }

                // Fault Chip
                item {
                    FilterChip(
                        selected = uiState.selectedCategoryFilter == "FAULT",
                        onClick = {
                            viewModel.setCategoryFilter(
                                if (uiState.selectedCategoryFilter == "FAULT") "ALL" else "FAULT"
                            )
                        },
                        label = { Text("🔴 Arıza") },
                        modifier = Modifier.testTag("filter_fault")
                    )
                }

                // Planned Chip
                item {
                    FilterChip(
                        selected = uiState.selectedCategoryFilter == "PLANNED",
                        onClick = {
                            viewModel.setCategoryFilter(
                                if (uiState.selectedCategoryFilter == "PLANNED") "ALL" else "PLANNED"
                            )
                        },
                        label = { Text("🟡 Planlı") },
                        modifier = Modifier.testTag("filter_planned")
                    )
                }
            }
        }

        // Outages List or Empty State
        if (filteredOutages.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = StatusGreenContainer,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = StatusGreen,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Kesinti Bulunmuyor ✅",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (uiState.selectedDistrictFilter == "Tüm İzmir") {
                                "Seçili filtre kriterlerine uygun kesinti kaydı bulunmamaktadır."
                            } else {
                                "${uiState.selectedDistrictFilter} ilçesinde şu an için kayıtlı bir kesinti bildirilmemiştir."
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.setDistrictFilter("Tüm İzmir")
                                viewModel.setTypeFilter("ALL")
                                viewModel.setCategoryFilter("ALL")
                                viewModel.setSearchQuery("")
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Tüm İzmir'i Göster")
                        }
                    }
                }
            }
        } else {
            items(filteredOutages, key = { it.id }) { outage ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    OutageCard(
                        outage = outage,
                        onCardClick = { viewModel.setSelectedOutage(outage) }
                    )
                }
            }
        }
    }

    // Dialogs
    if (uiState.showDistrictPicker) {
        DistrictSelectorDialog(
            selectedDistrict = uiState.selectedDistrictFilter,
            onDismissRequest = { viewModel.setShowDistrictPicker(false) },
            onDistrictSelected = { dist, neighborhood ->
                viewModel.setDistrictFilter(dist)
                viewModel.setStatusWidgetLocation(
                    if (dist == "Tüm İzmir") "Bornova" else dist,
                    neighborhood
                )
            }
        )
    }

    uiState.selectedOutageForDetail?.let { selectedOutage ->
        OutageDetailDialog(
            outage = selectedOutage,
            onDismissRequest = { viewModel.setSelectedOutage(null) }
        )
    }
}
