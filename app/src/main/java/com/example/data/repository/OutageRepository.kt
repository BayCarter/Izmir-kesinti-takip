package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.FavoriteAddressEntity
import com.example.data.local.NotificationLogEntity
import com.example.data.local.OutageEntity
import com.example.data.local.UserSettingsEntity
import com.example.model.IzmirLocations
import com.example.model.OutageCategory
import com.example.model.OutageItem
import com.example.model.OutageStatus
import com.example.model.OutageType
import com.example.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class OutageRepository(
    private val context: Context,
    private val database: AppDatabase = AppDatabase.getInstance(context),
    private val notificationHelper: NotificationHelper = NotificationHelper(context)
) {
    private val outageDao = database.outageDao()
    private val favoriteDao = database.favoriteAddressDao()
    private val notificationLogDao = database.notificationLogDao()
    private val userSettingsDao = database.userSettingsDao()

    val allOutagesFlow: Flow<List<OutageItem>> = combine(
        outageDao.getAllOutages(),
        favoriteDao.getAllFavorites()
    ) { outages, favorites ->
        outages.map { entity ->
            val isFav = favorites.any { fav ->
                fav.district.equals(entity.district, ignoreCase = true) &&
                        (entity.neighborhoods.isEmpty() || entity.neighborhoods.any { it.contains(fav.neighborhood, ignoreCase = true) })
            }
            entity.toModel(isFav)
        }
    }

    val favoritesFlow: Flow<List<FavoriteAddressEntity>> = favoriteDao.getAllFavorites()
    val notificationLogsFlow: Flow<List<NotificationLogEntity>> = notificationLogDao.getAllNotificationLogs()
    val userSettingsFlow: Flow<UserSettingsEntity?> = userSettingsDao.getUserSettings()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val count = favoriteDao.getCount()
        if (count == 0) {
            // Seed favorite places
            favoriteDao.insertFavorite(
                FavoriteAddressEntity(
                    label = "Evim",
                    district = "Bornova",
                    neighborhood = "Kazımdirik",
                    iconType = "HOME",
                    notifyWater = true,
                    notifyElectricity = true
                )
            )
            favoriteDao.insertFavorite(
                FavoriteAddressEntity(
                    label = "İş Yeri",
                    district = "Konak",
                    neighborhood = "Alsancak",
                    iconType = "WORK",
                    notifyWater = true,
                    notifyElectricity = true
                )
            )
            favoriteDao.insertFavorite(
                FavoriteAddressEntity(
                    label = "Yazlık",
                    district = "Çeşme",
                    neighborhood = "Alaçatı",
                    iconType = "SUMMER",
                    notifyWater = true,
                    notifyElectricity = true
                )
            )
        }

        val settings = userSettingsDao.getUserSettingsDirect()
        if (settings == null) {
            userSettingsDao.insertOrUpdateSettings(
                UserSettingsEntity(
                    id = 1,
                    selectedDistrict = "Tüm İzmir",
                    selectedNeighborhood = "",
                    notificationScope = "DISTRICT_WIDE",
                    waterNotificationsEnabled = true,
                    electricityNotificationsEnabled = true,
                    plannedOutagesEnabled = true,
                    faultOutagesEnabled = true,
                    autoRefreshMinutes = 10,
                    soundVibrateEnabled = true,
                    lastSyncTime = System.currentTimeMillis()
                )
            )
        }

        // Seed initial rich outages for Izmir
        seedOutagesData()
    }

    suspend fun seedOutagesData() {
        val now = System.currentTimeMillis()
        val hour = 3600 * 1000L

        val initialList = listOf(
            // 1. İZSU - Bornova Kazımdirik
            OutageEntity(
                id = "IZSU-2026-BOR-01",
                type = OutageType.WATER,
                category = OutageCategory.FAULT,
                district = "Bornova",
                neighborhoods = listOf("Kazımdirik", "Erzene"),
                title = "Kazımdirik & Erzene Ana Boru Arızası",
                reason = "572 Sokak içi Ø300 mm ana taşıyıcı boru patlağı nedeniyle basınç düşüklüğü ve su kesintisi yaşanmaktadır. Ekipler sahada müdahaleye başlamıştır.",
                startTime = now - (1 * hour),
                estimatedEndTime = now + (3 * hour),
                status = OutageStatus.ACTIVE,
                affectedSubscriberCount = 8400,
                announcementDate = now - (1 * hour)
            ),

            // 2. Gediz Elektrik - Karşıyaka Bostanlı
            OutageEntity(
                id = "GEDIZ-2026-KSK-01",
                type = OutageType.ELECTRICITY,
                category = OutageCategory.PLANNED,
                district = "Karşıyaka",
                neighborhoods = listOf("Bostanlı", "Mavişehir"),
                title = "Bostanlı & Mavişehir Şebeke Yenileme",
                reason = "Cemal Gürsel Caddesi trafo güçlendirme ve yeraltı kablo modernizasyon yatırımı kapsamında planlı kesinti uygulanmaktadır.",
                startTime = now - (30 * 60 * 1000L),
                estimatedEndTime = now + (2 * hour),
                status = OutageStatus.ACTIVE,
                affectedSubscriberCount = 12500,
                announcementDate = now - (24 * hour)
            ),

            // 3. İZSU - Buca Şirinyer
            OutageEntity(
                id = "IZSU-2026-BUC-01",
                type = OutageType.WATER,
                category = OutageCategory.FAULT,
                district = "Buca",
                neighborhoods = listOf("Şirinyer", "Efeler", "Kozağaç"),
                title = "Şirinyer Pompa İstasyonu Arızası",
                reason = "Menderes Caddesi terfi merkezi pompa arızası nedeniyle su verilememektedir. İZSU acil arıza ekipleri onarım çalışmasını sürdürmektedir.",
                startTime = now - (2 * hour),
                estimatedEndTime = now + (1 * hour + 45 * 60 * 1000L),
                status = OutageStatus.ACTIVE,
                affectedSubscriberCount = 16200,
                announcementDate = now - (2 * hour)
            ),

            // 4. Gediz Elektrik - Konak Alsancak
            OutageEntity(
                id = "GEDIZ-2026-KNK-01",
                type = OutageType.ELECTRICITY,
                category = OutageCategory.FAULT,
                district = "Konak",
                neighborhoods = listOf("Alsancak", "Kültür"),
                title = "Alsancak Gül Sokak Kablo Arızası",
                reason = "Gül Sokak ve 1382 Sokak kesişimindeki yeraltı kablo arızası nedeniyle acil onarım yapılmaktadır.",
                startTime = now - (45 * 60 * 1000L),
                estimatedEndTime = now + (1 * hour + 15 * 60 * 1000L),
                status = OutageStatus.ACTIVE,
                affectedSubscriberCount = 4200,
                announcementDate = now - (45 * 60 * 1000L)
            ),

            // 5. İZSU - Bayraklı Mansuroğlu
            OutageEntity(
                id = "IZSU-2026-BAY-01",
                type = OutageType.WATER,
                category = OutageCategory.PLANNED,
                district = "Bayraklı",
                neighborhoods = listOf("Mansuroğlu", "Adalet", "Manavkuyu"),
                title = "Bayraklı Bölge Vana Değişimi",
                reason = "İslam Kerimov Caddesi üzerinde yeni ana iletim hattı bağlantısı ve basınç regülatörü vana montajı çalışması.",
                startTime = now + (2 * hour),
                estimatedEndTime = now + (7 * hour),
                status = OutageStatus.UPCOMING,
                affectedSubscriberCount = 14500,
                announcementDate = now - (12 * hour)
            ),

            // 6. Gediz Elektrik - Çiğli Ataşehir
            OutageEntity(
                id = "GEDIZ-2026-CIG-01",
                type = OutageType.ELECTRICITY,
                category = OutageCategory.PLANNED,
                district = "Çiğli",
                neighborhoods = listOf("Ataşehir", "Balatçık"),
                title = "Çiğli Sanayi Bölgesi Trafo Bakımı",
                reason = "Yeni dağıtım merkezi bağlantısı ve trafo merkezleri periyodik test bakım çalışması.",
                startTime = now + (4 * hour),
                estimatedEndTime = now + (9 * hour),
                status = OutageStatus.UPCOMING,
                affectedSubscriberCount = 9800,
                announcementDate = now - (18 * hour)
            ),

            // 7. İZSU - Çeşme Alaçatı
            OutageEntity(
                id = "IZSU-2026-CES-01",
                type = OutageType.WATER,
                category = OutageCategory.FAULT,
                district = "Çeşme",
                neighborhoods = listOf("Alaçatı", "Ilıca"),
                title = "Alaçatı Çamlık Yol Şebeke Arızası",
                reason = "Ana dağıtım şebekesindeki bağlantı arızası giderilmektedir.",
                startTime = now - (3 * hour),
                estimatedEndTime = now + (30 * 60 * 1000L),
                status = OutageStatus.ACTIVE,
                affectedSubscriberCount = 3100,
                announcementDate = now - (3 * hour)
            ),

            // 8. Gediz Elektrik - Bornova Evka 3
            OutageEntity(
                id = "GEDIZ-2026-BOR-02",
                type = OutageType.ELECTRICITY,
                category = OutageCategory.FAULT,
                district = "Bornova",
                neighborhoods = listOf("Evka 3", "Işıklar"),
                title = "Evka 3 Yüksek Gerilim Hattı Arızası",
                reason = "Aşırı yük kaynaklı dağıtım panosu sigorta arızası nedeniyle acil müdahale edilmektedir.",
                startTime = now - (50 * 60 * 1000L),
                estimatedEndTime = now + (2 * hour),
                status = OutageStatus.ACTIVE,
                affectedSubscriberCount = 5600,
                announcementDate = now - (50 * 60 * 1000L)
            ),

            // Historical Outages (Resolved) - Son 30 gün
            OutageEntity(
                id = "IZSU-HIST-01",
                type = OutageType.WATER,
                category = OutageCategory.FAULT,
                district = "Konak",
                neighborhoods = listOf("Göztepe", "Güzelyalı"),
                title = "Göztepe Sahil Yolu İsale Hattı Onarımı",
                reason = "Mithatpaşa Caddesi üzerinde boru çatlağı onarıldı ve şebekeye su verildi.",
                startTime = now - (2 * 24 * hour),
                estimatedEndTime = now - (2 * 24 * hour - 4 * hour),
                status = OutageStatus.RESOLVED,
                affectedSubscriberCount = 11000,
                announcementDate = now - (2 * 24 * hour)
            ),
            OutageEntity(
                id = "GEDIZ-HIST-01",
                type = OutageType.ELECTRICITY,
                category = OutageCategory.PLANNED,
                district = "Karşıyaka",
                neighborhoods = listOf("Alaybey", "Bahçelievler"),
                title = "Alaybey Hat Yenileme Çalışması",
                reason = "Planlı direk ve trafo yenileme çalışması başarıyla tamamlandı.",
                startTime = now - (4 * 24 * hour),
                estimatedEndTime = now - (4 * 24 * hour - 5 * hour),
                status = OutageStatus.RESOLVED,
                affectedSubscriberCount = 7800,
                announcementDate = now - (5 * 24 * hour)
            ),
            OutageEntity(
                id = "IZSU-HIST-02",
                type = OutageType.WATER,
                category = OutageCategory.PLANNED,
                district = "Gaziemir",
                neighborhoods = listOf("Aktepe", "Emrez"),
                title = "Gaziemir Vana Montajı ve Hat Bakımı",
                reason = "İçme suyu şebekesi iyileştirme çalışmaları tamamlanmıştır.",
                startTime = now - (7 * 24 * hour),
                estimatedEndTime = now - (7 * 24 * hour - 3 * hour),
                status = OutageStatus.RESOLVED,
                affectedSubscriberCount = 6300,
                announcementDate = now - (8 * 24 * hour)
            ),
            OutageEntity(
                id = "GEDIZ-HIST-02",
                type = OutageType.ELECTRICITY,
                category = OutageCategory.FAULT,
                district = "Buca",
                neighborhoods = listOf("Buca Koop", "Yıldız"),
                title = "Buca Koop Trafo Arızası",
                reason = "Yeraltı güç kablosundaki deformasyon onarılmıştır.",
                startTime = now - (11 * 24 * hour),
                estimatedEndTime = now - (11 * 24 * hour - 2 * hour),
                status = OutageStatus.RESOLVED,
                affectedSubscriberCount = 8900,
                announcementDate = now - (11 * 24 * hour)
            ),
            OutageEntity(
                id = "IZSU-HIST-03",
                type = OutageType.WATER,
                category = OutageCategory.FAULT,
                district = "Urla",
                neighborhoods = listOf("İskele", "Kalabak"),
                title = "Urla İskele Ana Boru Onarımı",
                reason = "Deniz kıyısı iletim hattındaki sızıntı giderildi.",
                startTime = now - (16 * 24 * hour),
                estimatedEndTime = now - (16 * 24 * hour - 6 * hour),
                status = OutageStatus.RESOLVED,
                affectedSubscriberCount = 4100,
                announcementDate = now - (16 * 24 * hour)
            )
        )

        outageDao.insertOutages(initialList)
    }

    suspend fun refreshDataFromInstitutions(): Boolean {
        // Simulates real-time scraper / API query from İZSU & Gediz Elektrik portals
        val now = System.currentTimeMillis()
        val hour = 3600 * 1000L

        // Update settings lastSyncTime
        val currentSettings = userSettingsDao.getUserSettingsDirect() ?: UserSettingsEntity()
        userSettingsDao.insertOrUpdateSettings(currentSettings.copy(lastSyncTime = now))

        // Create a new simulated real-time outage in randomly selected Izmir district if needed
        val sampleDistricts = listOf("Bornova", "Karşıyaka", "Konak", "Buca", "Bayraklı", "Balçova", "Narlıdere", "Gaziemir", "Çiğli", "Urla")
        val randomDistrict = sampleDistricts.random()
        val neighborhoods = IzmirLocations.getNeighborhoodsForDistrict(randomDistrict).take(2)
        val isWater = (0..1).random() == 0

        val newOutage = if (isWater) {
            OutageEntity(
                id = "IZSU-LIVE-${System.currentTimeMillis() % 10000}",
                type = OutageType.WATER,
                category = OutageCategory.FAULT,
                district = randomDistrict,
                neighborhoods = neighborhoods,
                title = "$randomDistrict ${neighborhoods.firstOrNull() ?: ""} Su Arızası",
                reason = "İçme suyu dağıtım şebekesinde oluşan arıza nedeniyle geçici su kesintisi yaşanmaktadır. İZSU ekipleri sahada çalışmaktadır.",
                startTime = now,
                estimatedEndTime = now + (2 * hour + 30 * 60 * 1000L),
                status = OutageStatus.ACTIVE,
                affectedSubscriberCount = (3000..9000).random(),
                announcementDate = now
            )
        } else {
            OutageEntity(
                id = "GEDIZ-LIVE-${System.currentTimeMillis() % 10000}",
                type = OutageType.ELECTRICITY,
                category = OutageCategory.PLANNED,
                district = randomDistrict,
                neighborhoods = neighborhoods,
                title = "$randomDistrict ${neighborhoods.firstOrNull() ?: ""} Şebeke Bakımı",
                reason = "Gediz Elektrik yatırım ve bakım programı kapsamında trafo ve dağıtım panolarında çalışma yapılmaktadır.",
                startTime = now + (30 * 60 * 1000L),
                estimatedEndTime = now + (4 * hour),
                status = OutageStatus.UPCOMING,
                affectedSubscriberCount = (4000..12000).random(),
                announcementDate = now
            )
        }

        outageDao.insertOutage(newOutage)

        // Check if this new outage affects user preferences or favorites
        val favorites = favoriteDao.getAllFavorites().first()
        val isFavAffected = favorites.any { it.district.equals(randomDistrict, ignoreCase = true) }

        if (currentSettings.soundVibrateEnabled && (currentSettings.notificationScope == "ALL_IZMIR" || isFavAffected)) {
            val outageModel = newOutage.toModel(isFavAffected)
            notificationHelper.showOutageNotification(outageModel)

            // Log notification
            notificationLogDao.insertNotification(
                NotificationLogEntity(
                    outageId = newOutage.id,
                    title = outageModel.title,
                    message = "${newOutage.district} / ${newOutage.neighborhoods.joinToString(", ")}: ${newOutage.reason}",
                    type = newOutage.type,
                    district = newOutage.district,
                    neighborhood = newOutage.neighborhoods.firstOrNull() ?: "",
                    timestamp = now
                )
            )
        }

        return true
    }

    suspend fun addFavorite(label: String, district: String, neighborhood: String, iconType: String = "HOME") {
        favoriteDao.insertFavorite(
            FavoriteAddressEntity(
                label = label,
                district = district,
                neighborhood = neighborhood,
                iconType = iconType,
                notifyWater = true,
                notifyElectricity = true
            )
        )
    }

    suspend fun updateFavorite(favorite: FavoriteAddressEntity) {
        favoriteDao.updateFavorite(favorite)
    }

    suspend fun deleteFavorite(id: Long) {
        favoriteDao.deleteFavoriteById(id)
    }

    suspend fun updateUserSettings(settings: UserSettingsEntity) {
        userSettingsDao.insertOrUpdateSettings(settings)
    }

    suspend fun triggerTestNotification(type: OutageType, district: String, neighborhood: String) {
        notificationHelper.sendTestNotification(type, district, neighborhood)
        notificationLogDao.insertNotification(
            NotificationLogEntity(
                outageId = "TEST-${System.currentTimeMillis()}",
                title = "[Test] ${type.title} (${type.shortName})",
                message = "$district / $neighborhood kesinti uyarısı simülasyonu başarıyla gönderildi.",
                type = type,
                district = district,
                neighborhood = neighborhood,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun markNotificationAsRead(id: Long) {
        notificationLogDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        notificationLogDao.markAllAsRead()
    }

    suspend fun clearNotificationLogs() {
        notificationLogDao.clearAllLogs()
    }
}
