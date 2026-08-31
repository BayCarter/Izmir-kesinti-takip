package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.IzmirDistrict
import com.example.model.IzmirLocations
import com.example.model.OutageCategory
import com.example.model.OutageItem
import com.example.model.OutageStatus
import com.example.model.OutageType
import com.example.ui.components.OutageCard
import com.example.ui.theme.GedizAmber
import com.example.ui.theme.GedizAmberContainer
import com.example.ui.theme.IzmirBlue
import com.example.ui.theme.IzmirBlueContainer
import com.example.ui.theme.IzmirNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenContainer
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedContainer
import com.example.ui.theme.StatusYellow
import com.example.ui.theme.StatusYellowContainer
import com.example.ui.viewmodel.DistrictStatus
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val districtStatusList by viewModel.districtStatusList.collectAsStateWithLifecycle()
    val allOutages by viewModel.allOutages.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, WATER, ELECTRICITY
    var searchQuery by remember { mutableStateOf("") }
    var selectedDistrictDetail by remember { mutableStateOf<DistrictStatus?>(null) }

    val filteredDistrictStatusList = remember(districtStatusList, selectedFilter, searchQuery) {
        districtStatusList.filter { item ->
            val matchesFilter = when (selectedFilter) {
                "WATER" -> item.waterOutages > 0 || (item.totalActiveOutages == 0)
                "ELECTRICITY" -> item.electricityOutages > 0 || (item.totalActiveOutages == 0)
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() || item.district.name.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("map_screen_list"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(IzmirNavy)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "İzmir Kesinti Haritası",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "30 İlçenin Anlık Şebeke ve Arıza Durumu",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = IzmirBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Map Legend (🟢 Kesinti yok, 🟡 Planlı kesinti, 🔴 Arıza)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendItem(color = StatusGreen, text = "🟢 Kesinti Yok")
                        LegendItem(color = StatusYellow, text = "🟡 Planlı Bakım")
                        LegendItem(color = StatusRed, text = "🔴 Arıza / Kesinti")
                    }
                }
            }
        }

        // Interactive Map Visualization Canvas (Aegean Gulf + District Nodes)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Canvas drawing representing Izmir Gulf coastline and geographic nodes
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            val w = size.width
                            val h = size.height

                            // Draw stylized Aegean Sea gulf
                            val seaPath = Path().apply {
                                moveTo(0f, h * 0.4f)
                                cubicTo(w * 0.25f, h * 0.45f, w * 0.35f, h * 0.6f, w * 0.45f, h * 0.48f)
                                cubicTo(w * 0.55f, h * 0.35f, w * 0.7f, h * 0.4f, w, h * 0.25f)
                                lineTo(w, 0f)
                                lineTo(0f, 0f)
                                close()
                            }
                            drawPath(
                                path = seaPath,
                                color = IzmirBlue.copy(alpha = 0.12f)
                            )
                            drawPath(
                                path = seaPath,
                                color = IzmirBlue.copy(alpha = 0.4f),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }

                        // Overlay Interactive District Nodes
                        DistrictMapNodes(
                            districtStatusList = districtStatusList,
                            onDistrictClick = { districtStatus ->
                                selectedDistrictDetail = districtStatus
                            }
                        )
                    }
                }
            }
        }

        // Filter chips and search for district list
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Search box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("map_district_search_input"),
                    placeholder = { Text("İlçe adı ile haritada ara...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("Tümü (30 İlçe)") }
                    )
                    FilterChip(
                        selected = selectedFilter == "WATER",
                        onClick = { selectedFilter = if (selectedFilter == "WATER") "ALL" else "WATER" },
                        leadingIcon = { Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, tint = IzmirBlue, modifier = Modifier.size(16.dp)) },
                        label = { Text("💧 İZSU") }
                    )
                    FilterChip(
                        selected = selectedFilter == "ELECTRICITY",
                        onClick = { selectedFilter = if (selectedFilter == "ELECTRICITY") "ALL" else "ELECTRICITY" },
                        leadingIcon = { Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = GedizAmber, modifier = Modifier.size(16.dp)) },
                        label = { Text("⚡ Gediz") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "İlçe Durum Listesi (${filteredDistrictStatusList.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }

        // District Cards Grid/List
        items(filteredDistrictStatusList, key = { it.district.name }) { item ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                DistrictStatusRowCard(
                    status = item,
                    onClick = { selectedDistrictDetail = item }
                )
            }
        }
    }

    // District Outages Detail Bottom Sheet
    selectedDistrictDetail?.let { detail ->
        val districtOutages = remember(allOutages, detail) {
            allOutages.filter {
                it.district.equals(detail.district.name, ignoreCase = true) &&
                        it.status != OutageStatus.RESOLVED
            }
        }

        ModalBottomSheet(
            onDismissRequest = { selectedDistrictDetail = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Sheet Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${detail.district.name} İlçesi",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Nüfus: ${detail.district.population} • ${detail.district.neighborhoods.size} Mahalle",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    IconButton(onClick = { selectedDistrictDetail = null }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Kapat")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Outages count summary banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (detail.statusColor) {
                        "RED" -> StatusRedContainer
                        "YELLOW" -> StatusYellowContainer
                        else -> StatusGreenContainer
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = when (detail.statusColor) {
                                "RED" -> "🔴 ${detail.totalActiveOutages} Aktif Arıza / Kesinti Var"
                                "YELLOW" -> "🟡 ${detail.totalActiveOutages} Planlı Bakım Kesintisi"
                                else -> "🟢 Kesinti Yok - Tüm Şebekeler Normal"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (detail.statusColor) {
                                    "RED" -> StatusRed
                                    "YELLOW" -> StatusYellow
                                    else -> StatusGreen
                                }
                            )
                        )

                        Text(
                            text = "💧 ${detail.waterOutages}  ⚡ ${detail.electricityOutages}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (districtOutages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Bu ilçede şu an aktif bir kesinti bulunmuyor. ✅",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.height(340.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(districtOutages) { outage ->
                            OutageCard(
                                outage = outage,
                                onCardClick = {
                                    selectedDistrictDetail = null
                                    viewModel.setSelectedOutage(outage)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun DistrictMapNodes(
    districtStatusList: List<DistrictStatus>,
    onDistrictClick: (DistrictStatus) -> Unit
) {
    // Key prominent Izmir districts positioned relatively on visual map grid
    val keyNodes = listOf(
        Pair("Bornova", Offset(0.68f, 0.42f)),
        Pair("Karşıyaka", Offset(0.48f, 0.32f)),
        Pair("Konak", Offset(0.52f, 0.52f)),
        Pair("Buca", Offset(0.65f, 0.65f)),
        Pair("Bayraklı", Offset(0.60f, 0.38f)),
        Pair("Çiğli", Offset(0.38f, 0.22f)),
        Pair("Balçova", Offset(0.38f, 0.58f)),
        Pair("Narlıdere", Offset(0.28f, 0.60f)),
        Pair("Gaziemir", Offset(0.55f, 0.78f)),
        Pair("Urla", Offset(0.18f, 0.70f)),
        Pair("Çeşme", Offset(0.06f, 0.78f)),
        Pair("Menemen", Offset(0.42f, 0.12f))
    )

    Box(modifier = Modifier.fillMaxSize()) {
        keyNodes.forEach { (districtName, relativePos) ->
            val status = districtStatusList.firstOrNull { it.district.name == districtName }
            if (status != null) {
                val nodeColor = when (status.statusColor) {
                    "RED" -> StatusRed
                    "YELLOW" -> StatusYellow
                    else -> StatusGreen
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = (relativePos.x * 290).dp,
                            top = (relativePos.y * 220).dp
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                        .clickable { onDistrictClick(status) }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(nodeColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = districtName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DistrictStatusRowCard(
    status: DistrictStatus,
    onClick: () -> Unit
) {
    val statusColor = when (status.statusColor) {
        "RED" -> StatusRed
        "YELLOW" -> StatusYellow
        else -> StatusGreen
    }

    val statusBg = when (status.statusColor) {
        "RED" -> StatusRedContainer
        "YELLOW" -> StatusYellowContainer
        else -> StatusGreenContainer
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("district_status_${status.district.name}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(statusColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = status.district.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${status.district.neighborhoods.size} Mahalle • Nüfus: ${status.district.population}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusBg
            ) {
                Text(
                    text = when (status.statusColor) {
                        "RED" -> "🔴 ${status.totalActiveOutages} Arıza"
                        "YELLOW" -> "🟡 ${status.totalActiveOutages} Planlı"
                        else -> "🟢 Kesinti Yok"
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                )
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        )
    }
}
