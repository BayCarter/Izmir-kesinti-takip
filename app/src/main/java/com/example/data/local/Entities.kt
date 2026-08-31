package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.OutageCategory
import com.example.model.OutageItem
import com.example.model.OutageStatus
import com.example.model.OutageType

@Entity(tableName = "outages")
data class OutageEntity(
    @PrimaryKey val id: String,
    val type: OutageType,
    val category: OutageCategory,
    val district: String,
    val neighborhoods: List<String>,
    val title: String,
    val reason: String,
    val startTime: Long,
    val estimatedEndTime: Long,
    val status: OutageStatus,
    val affectedSubscriberCount: Int,
    val announcementDate: Long
) {
    fun toModel(isFavoriteAffected: Boolean = false): OutageItem {
        return OutageItem(
            id = id,
            type = type,
            category = category,
            district = district,
            neighborhoods = neighborhoods,
            title = title,
            reason = reason,
            startTime = startTime,
            estimatedEndTime = estimatedEndTime,
            status = status,
            affectedSubscriberCount = affectedSubscriberCount,
            announcementDate = announcementDate,
            isFavoriteAddressAffected = isFavoriteAffected
        )
    }

    companion object {
        fun fromModel(model: OutageItem): OutageEntity {
            return OutageEntity(
                id = model.id,
                type = model.type,
                category = model.category,
                district = model.district,
                neighborhoods = model.neighborhoods,
                title = model.title,
                reason = model.reason,
                startTime = model.startTime,
                estimatedEndTime = model.estimatedEndTime,
                status = model.status,
                affectedSubscriberCount = model.affectedSubscriberCount,
                announcementDate = model.announcementDate
            )
        }
    }
}

@Entity(tableName = "favorite_addresses")
data class FavoriteAddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String, // e.g. "Evim", "İş Yeri", "Yazlık"
    val district: String, // e.g. "Bornova"
    val neighborhood: String, // e.g. "Kazımdirik"
    val iconType: String = "HOME", // HOME, WORK, SUMMER, FAMILY, OTHER
    val notifyWater: Boolean = true,
    val notifyElectricity: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notification_logs")
data class NotificationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val outageId: String,
    val title: String,
    val message: String,
    val type: OutageType,
    val district: String,
    val neighborhood: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val selectedDistrict: String = "Tüm İzmir",
    val selectedNeighborhood: String = "",
    val notificationScope: String = "DISTRICT_WIDE", // NEIGHBORHOOD_ONLY, DISTRICT_WIDE, ALL_IZMIR
    val waterNotificationsEnabled: Boolean = true,
    val electricityNotificationsEnabled: Boolean = true,
    val plannedOutagesEnabled: Boolean = true,
    val faultOutagesEnabled: Boolean = true,
    val autoRefreshMinutes: Int = 10,
    val soundVibrateEnabled: Boolean = true,
    val lastSyncTime: Long = System.currentTimeMillis()
)
