package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.OutageStatus
import com.example.model.OutageType
import kotlinx.coroutines.flow.Flow

@Dao
interface OutageDao {
    @Query("SELECT * FROM outages ORDER BY startTime DESC")
    fun getAllOutages(): Flow<List<OutageEntity>>

    @Query("SELECT * FROM outages WHERE status != 'RESOLVED' ORDER BY startTime DESC")
    fun getActiveAndUpcomingOutages(): Flow<List<OutageEntity>>

    @Query("SELECT * FROM outages WHERE status = 'RESOLVED' ORDER BY estimatedEndTime DESC")
    fun getResolvedOutages(): Flow<List<OutageEntity>>

    @Query("SELECT * FROM outages WHERE district = :district ORDER BY startTime DESC")
    fun getOutagesByDistrict(district: String): Flow<List<OutageEntity>>

    @Query("SELECT * FROM outages WHERE type = :type ORDER BY startTime DESC")
    fun getOutagesByType(type: OutageType): Flow<List<OutageEntity>>

    @Query("SELECT * FROM outages WHERE id = :id LIMIT 1")
    suspend fun getOutageById(id: String): OutageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutages(outages: List<OutageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutage(outage: OutageEntity)

    @Update
    suspend fun updateOutage(outage: OutageEntity)

    @Query("DELETE FROM outages WHERE id = :id")
    suspend fun deleteOutageById(id: String)

    @Query("DELETE FROM outages")
    suspend fun clearAll()
}

@Dao
interface FavoriteAddressDao {
    @Query("SELECT * FROM favorite_addresses ORDER BY createdAt ASC")
    fun getAllFavorites(): Flow<List<FavoriteAddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteAddressEntity): Long

    @Update
    suspend fun updateFavorite(favorite: FavoriteAddressEntity)

    @Query("DELETE FROM favorite_addresses WHERE id = :id")
    suspend fun deleteFavoriteById(id: Long)

    @Query("SELECT COUNT(*) FROM favorite_addresses")
    suspend fun getCount(): Int
}

@Dao
interface NotificationLogDao {
    @Query("SELECT * FROM notification_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllNotificationLogs(): Flow<List<NotificationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(log: NotificationLogEntity)

    @Query("UPDATE notification_logs SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notification_logs SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notification_logs")
    suspend fun clearAllLogs()
}

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getUserSettings(): Flow<UserSettingsEntity?>

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    suspend fun getUserSettingsDirect(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: UserSettingsEntity)
}
