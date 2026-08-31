package com.example.data.local

import androidx.room.TypeConverter
import com.example.model.OutageCategory
import com.example.model.OutageStatus
import com.example.model.OutageType

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString("|||") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split("|||").filter { it.isNotBlank() }
    }

    @TypeConverter
    fun fromOutageType(value: OutageType): String = value.name

    @TypeConverter
    fun toOutageType(value: String): OutageType = try {
        OutageType.valueOf(value)
    } catch (e: Exception) {
        OutageType.WATER
    }

    @TypeConverter
    fun fromOutageCategory(value: OutageCategory): String = value.name

    @TypeConverter
    fun toOutageCategory(value: String): OutageCategory = try {
        OutageCategory.valueOf(value)
    } catch (e: Exception) {
        OutageCategory.FAULT
    }

    @TypeConverter
    fun fromOutageStatus(value: OutageStatus): String = value.name

    @TypeConverter
    fun toOutageStatus(value: String): OutageStatus = try {
        OutageStatus.valueOf(value)
    } catch (e: Exception) {
        OutageStatus.ACTIVE
    }
}
