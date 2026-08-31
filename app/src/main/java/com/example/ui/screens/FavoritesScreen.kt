package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.FavoriteAddressEntity
import com.example.model.OutageItem
import com.example.model.OutageStatus
import com.example.model.OutageType
import com.example.ui.components.AddFavoriteDialog
import com.example.ui.theme.GedizAmber
import com.example.ui.theme.IzmirBlue
import com.example.ui.theme.IzmirNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenContainer
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedContainer
import com.example.ui.theme.StatusYellow
import com.example.ui.theme.StatusYellowContainer
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FavoritesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val allOutages by viewModel.allOutages.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.setShowAddFavoriteDialog(true) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("add_favorite_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Ekle")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Yeni Adres", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("favorites_screen_list"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header Banner
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IzmirNavy)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Kayıtlı Adreslerim",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Ev, iş yeri ve yazlığınızın anlık kesinti takibi",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GedizAmber,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            if (favorites.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Henüz Favori Adres Eklenmedi",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Ev, iş yeri veya yazlık adresinizi ekleyerek su ve elektrik kesintilerini anında öğrenin.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.setShowAddFavoriteDialog(true) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("+ Adres Ekle")
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = "Kayıtlı Konumlar (${favorites.size})",
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                items(favorites, key = { it.id }) { favorite ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        FavoriteAddressCard(
                            favorite = favorite,
                            allOutages = allOutages,
                            onDeleteClick = { viewModel.deleteFavorite(favorite.id) },
                            onToggleWater = { isChecked ->
                                viewModel.updateFavorite(favorite.copy(notifyWater = isChecked))
                            },
                            onToggleElectricity = { isChecked ->
                                viewModel.updateFavorite(favorite.copy(notifyElectricity = isChecked))
                            },
                            onCardClick = {
                                viewModel.setStatusWidgetLocation(favorite.district, favorite.neighborhood)
                                viewModel.setDistrictFilter(favorite.district)
                            }
                        )
                    }
                }
            }
        }
    }

    if (uiState.showAddFavoriteDialog) {
        AddFavoriteDialog(
            onDismissRequest = { viewModel.setShowAddFavoriteDialog(false) },
            onConfirm = { label, dist, n, iconType ->
                viewModel.addFavorite(label, dist, n, iconType)
            }
        )
    }
}

@Composable
private fun FavoriteAddressCard(
    favorite: FavoriteAddressEntity,
    allOutages: List<OutageItem>,
    onDeleteClick: () -> Unit,
    onToggleWater: (Boolean) -> Unit,
    onToggleElectricity: (Boolean) -> Unit,
    onCardClick: () -> Unit
) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale("tr")) }

    val iconVector: ImageVector = when (favorite.iconType) {
        "WORK" -> Icons.Default.Business
        "SUMMER" -> Icons.Default.BeachAccess
        "FAMILY" -> Icons.Default.FamilyRestroom
        else -> Icons.Default.Home
    }

    // Check outages for this address
    val waterOutage = allOutages.firstOrNull {
        it.type == OutageType.WATER &&
                it.status != OutageStatus.RESOLVED &&
                it.district.equals(favorite.district, ignoreCase = true) &&
                (it.neighborhoods.isEmpty() || it.neighborhoods.any { n -> n.contains(favorite.neighborhood, ignoreCase = true) })
    }

    val elecOutage = allOutages.firstOrNull {
        it.type == OutageType.ELECTRICITY &&
                it.status != OutageStatus.RESOLVED &&
                it.district.equals(favorite.district, ignoreCase = true) &&
                (it.neighborhoods.isEmpty() || it.neighborhoods.any { n -> n.contains(favorite.neighborhood, ignoreCase = true) })
    }

    val hasAnyOutage = waterOutage != null || elecOutage != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onCardClick() }
            .testTag("favorite_card_${favorite.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            1.dp,
            if (hasAnyOutage) StatusRed.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Icon + Label + Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = favorite.label,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${favorite.district} / ${favorite.neighborhood}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.testTag("delete_favorite_${favorite.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Sil",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two-column status indicators for Water and Electricity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Water Status
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (waterOutage != null) StatusRedContainer.copy(alpha = 0.7f) else StatusGreenContainer.copy(alpha = 0.7f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💧 İZSU Su",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Icon(
                                imageVector = if (waterOutage != null) Icons.Default.ErrorOutline else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (waterOutage != null) StatusRed else StatusGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (waterOutage != null) "Kesinti Var (${timeFormat.format(Date(waterOutage.estimatedEndTime))})" else "Kesinti Yok ✅",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (waterOutage != null) StatusRed else StatusGreen,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Electricity Status
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (elecOutage != null) StatusRedContainer.copy(alpha = 0.7f) else StatusGreenContainer.copy(alpha = 0.7f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚡ Gediz Elektrik",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Icon(
                                imageVector = if (elecOutage != null) Icons.Default.ErrorOutline else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (elecOutage != null) StatusRed else StatusGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (elecOutage != null) "Kesinti Var (${timeFormat.format(Date(elecOutage.estimatedEndTime))})" else "Kesinti Yok ✅",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (elecOutage != null) StatusRed else StatusGreen,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Notification Toggles for this address
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bu adres için bildirim al",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (favorite.notifyWater || favorite.notifyElectricity) "Aktif" else "Kapalı",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = favorite.notifyWater || favorite.notifyElectricity,
                        onCheckedChange = { isChecked ->
                            onToggleWater(isChecked)
                            onToggleElectricity(isChecked)
                        }
                    )
                }
            }
        }
    }
}
