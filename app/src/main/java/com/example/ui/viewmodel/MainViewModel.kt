package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FavoriteAddressEntity
import com.example.data.local.NotificationLogEntity
import com.example.data.local.UserSettingsEntity
import com.example.data.repository.OutageRepository
import com.example.model.IzmirDistrict
import com.example.model.IzmirLocations
import com.example.model.OutageCategory
import com.example.model.OutageItem
import com.example.model.OutageStatus
import com.example.model.OutageType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    MAP,
    FAVORITES,
    HISTORY,
    SETTINGS
}

data class DistrictStatus(
    val district: IzmirDistrict,
    val totalActiveOutages: Int,
    val waterOutages: Int,
    val electricityOutages: Int,
    val hasFault: Boolean,
    val hasPlanned: Boolean,
    val statusColor: String // "GREEN", "YELLOW", "RED"
)

data class OutageStats(
    val totalActive: Int = 0,
    val totalWater: Int = 0,
    val totalElectricity: Int = 0,
    val totalResolvedThisMonth: Int = 0,
    val avgRepairHours: Double = 0.0,
    val mostAffectedDistrict: String = "-"
)

data class MainUiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    val selectedDistrictFilter: String = "Tüm İzmir",
    val selectedTypeFilter: String = "ALL", // "ALL", "WATER", "ELECTRICITY"
    val selectedCategoryFilter: String = "ALL", // "ALL", "FAULT", "PLANNED"
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,
    val isScrapingSimulated: Boolean = false,
    val syncSuccessMessage: String? = null,
    val selectedOutageForDetail: OutageItem? = null,
    val selectedDistrictForMap: String? = null,
    val showDistrictPicker: Boolean = false,
    val showAddFavoriteDialog: Boolean = false,
    val showNotificationTestDialog: Boolean = false,
    val statusWidgetDistrict: String = "Bornova",
    val statusWidgetNeighborhood: String = "Kazımdirik"
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OutageRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    val allOutages: StateFlow<List<OutageItem>> = repository.allOutagesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<FavoriteAddressEntity>> = repository.favoritesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notificationLogs: StateFlow<List<NotificationLogEntity>> = repository.notificationLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSettings: StateFlow<UserSettingsEntity?> = repository.userSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Filtered active & upcoming outages
    val filteredOutages: StateFlow<List<OutageItem>> = combine(
        allOutages,
        _uiState
    ) { outages, state ->
        outages.filter { outage ->
            val matchesScreenStatus = outage.status != OutageStatus.RESOLVED

            val matchesDistrict = if (state.selectedDistrictFilter == "Tüm İzmir") {
                true
            } else {
                outage.district.equals(state.selectedDistrictFilter, ignoreCase = true)
            }

            val matchesType = when (state.selectedTypeFilter) {
                "WATER" -> outage.type == OutageType.WATER
                "ELECTRICITY" -> outage.type == OutageType.ELECTRICITY
                else -> true
            }

            val matchesCategory = when (state.selectedCategoryFilter) {
                "FAULT" -> outage.category == OutageCategory.FAULT
                "PLANNED" -> outage.category == OutageCategory.PLANNED
                else -> true
            }

            val matchesSearch = if (state.searchQuery.isBlank()) {
                true
            } else {
                outage.district.contains(state.searchQuery, ignoreCase = true) ||
                        outage.neighborhoods.any { it.contains(state.searchQuery, ignoreCase = true) } ||
                        outage.title.contains(state.searchQuery, ignoreCase = true) ||
                        outage.reason.contains(state.searchQuery, ignoreCase = true)
            }

            matchesScreenStatus && matchesDistrict && matchesType && matchesCategory && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Historical resolved outages
    val resolvedOutages: StateFlow<List<OutageItem>> = combine(
        allOutages,
        _uiState
    ) { outages, state ->
        outages.filter { outage ->
            val matchesStatus = outage.status == OutageStatus.RESOLVED

            val matchesDistrict = if (state.selectedDistrictFilter == "Tüm İzmir") {
                true
            } else {
                outage.district.equals(state.selectedDistrictFilter, ignoreCase = true)
            }

            val matchesSearch = if (state.searchQuery.isBlank()) {
                true
            } else {
                outage.district.contains(state.searchQuery, ignoreCase = true) ||
                        outage.neighborhoods.any { it.contains(state.searchQuery, ignoreCase = true) } ||
                        outage.title.contains(state.searchQuery, ignoreCase = true)
            }

            matchesStatus && matchesDistrict && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // District status for interactive map
    val districtStatusList: StateFlow<List<DistrictStatus>> = allOutages.combine(_uiState) { outages, state ->
        val activeOutages = outages.filter { it.status != OutageStatus.RESOLVED }

        IzmirLocations.ALL_DISTRICTS.map { district ->
            val districtOutages = activeOutages.filter {
                it.district.equals(district.name, ignoreCase = true)
            }

            val waterCount = districtOutages.count { it.type == OutageType.WATER }
            val elecCount = districtOutages.count { it.type == OutageType.ELECTRICITY }
            val hasFault = districtOutages.any { it.category == OutageCategory.FAULT }
            val hasPlanned = districtOutages.any { it.category == OutageCategory.PLANNED }

            val statusColor = when {
                hasFault -> "RED"
                hasPlanned -> "YELLOW"
                else -> "GREEN"
            }

            DistrictStatus(
                district = district,
                totalActiveOutages = districtOutages.size,
                waterOutages = waterCount,
                electricityOutages = elecCount,
                hasFault = hasFault,
                hasPlanned = hasPlanned,
                statusColor = statusColor
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Statistics calculation
    val stats: StateFlow<OutageStats> = allOutages.combine(resolvedOutages) { all, resolved ->
        val active = all.filter { it.status != OutageStatus.RESOLVED }
        val waterCount = active.count { it.type == OutageType.WATER }
        val elecCount = active.count { it.type == OutageType.ELECTRICITY }

        val totalResolved = resolved.size
        val avgDurationHours = if (resolved.isNotEmpty()) {
            val totalHours = resolved.sumOf { (it.estimatedEndTime - it.startTime).toDouble() / (1000 * 3600) }
            String.format(java.util.Locale.US, "%.1f", totalHours / resolved.size).toDoubleOrNull() ?: 3.5
        } else {
            3.5
        }

        val districtCounts = active.groupBy { it.district }.mapValues { it.value.size }
        val mostAffected = districtCounts.maxByOrNull { it.value }?.key ?: "Bornova"

        OutageStats(
            totalActive = active.size,
            totalWater = waterCount,
            totalElectricity = elecCount,
            totalResolvedThisMonth = totalResolved,
            avgRepairHours = avgDurationHours,
            mostAffectedDistrict = mostAffected
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OutageStats())

    fun setScreen(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun setDistrictFilter(district: String) {
        _uiState.update { it.copy(selectedDistrictFilter = district) }
    }

    fun setTypeFilter(type: String) {
        _uiState.update { it.copy(selectedTypeFilter = type) }
    }

    fun setCategoryFilter(category: String) {
        _uiState.update { it.copy(selectedCategoryFilter = category) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setSelectedOutage(outage: OutageItem?) {
        _uiState.update { it.copy(selectedOutageForDetail = outage) }
    }

    fun setSelectedDistrictForMap(district: String?) {
        _uiState.update { it.copy(selectedDistrictForMap = district) }
    }

    fun setShowDistrictPicker(show: Boolean) {
        _uiState.update { it.copy(showDistrictPicker = show) }
    }

    fun setShowAddFavoriteDialog(show: Boolean) {
        _uiState.update { it.copy(showAddFavoriteDialog = show) }
    }

    fun setShowNotificationTestDialog(show: Boolean) {
        _uiState.update { it.copy(showNotificationTestDialog = show) }
    }

    fun setStatusWidgetLocation(district: String, neighborhood: String) {
        _uiState.update {
            it.copy(statusWidgetDistrict = district, statusWidgetNeighborhood = neighborhood)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, isScrapingSimulated = true) }
            delay(1000) // Simulating network & web scrape latency from İZSU/Gediz
            repository.refreshDataFromInstitutions()
            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    isScrapingSimulated = false,
                    syncSuccessMessage = "İZSU & Gediz verileri güncellendi (${System.currentTimeMillis()})"
                )
            }
            delay(3000)
            _uiState.update { it.copy(syncSuccessMessage = null) }
        }
    }

    fun addFavorite(label: String, district: String, neighborhood: String, iconType: String) {
        viewModelScope.launch {
            repository.addFavorite(label, district, neighborhood, iconType)
            setShowAddFavoriteDialog(false)
        }
    }

    fun deleteFavorite(id: Long) {
        viewModelScope.launch {
            repository.deleteFavorite(id)
        }
    }

    fun updateFavorite(favorite: FavoriteAddressEntity) {
        viewModelScope.launch {
            repository.updateFavorite(favorite)
        }
    }

    fun updateUserSettings(settings: UserSettingsEntity) {
        viewModelScope.launch {
            repository.updateUserSettings(settings)
        }
    }

    fun sendTestNotification(type: OutageType, district: String, neighborhood: String) {
        viewModelScope.launch {
            repository.triggerTestNotification(type, district, neighborhood)
        }
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun clearAllNotificationLogs() {
        viewModelScope.launch {
            repository.clearNotificationLogs()
        }
    }
}
