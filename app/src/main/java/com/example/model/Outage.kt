package com.example.model

enum class OutageType(val title: String, val provider: String, val shortName: String, val contactPhone: String) {
    WATER("Su Kesintisi", "İZSU Genel Müdürlüğü", "İZSU", "185"),
    ELECTRICITY("Elektrik Kesintisi", "Gediz Elektrik Dağıtım A.Ş.", "Gediz", "186")
}

enum class OutageCategory(val label: String) {
    FAULT("Arıza / Onarım"),
    PLANNED("Planlı Bakım / Yatırım")
}

enum class OutageStatus(val label: String) {
    ACTIVE("Devam Ediyor"),
    UPCOMING("Planlandı"),
    RESOLVED("Giderildi")
}

data class OutageItem(
    val id: String,
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
    val announcementDate: Long,
    val isFavoriteAddressAffected: Boolean = false
) {
    val durationMinutes: Long
        get() = ((estimatedEndTime - startTime) / (1000 * 60)).coerceAtLeast(0)

    val remainingMinutes: Long
        get() {
            val now = System.currentTimeMillis()
            return if (now >= estimatedEndTime) 0 else ((estimatedEndTime - now) / (1000 * 60))
        }

    val progressPercent: Float
        get() {
            val now = System.currentTimeMillis()
            if (now <= startTime) return 0f
            if (now >= estimatedEndTime) return 1f
            val total = (estimatedEndTime - startTime).toFloat()
            if (total <= 0f) return 1f
            return ((now - startTime) / total).coerceIn(0f, 1f)
        }
}
