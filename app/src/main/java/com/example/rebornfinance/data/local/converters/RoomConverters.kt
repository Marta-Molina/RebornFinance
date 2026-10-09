package com.example.rebornfinance.data.local.converters

import androidx.room.TypeConverter
import com.example.rebornfinance.domain.model.MovementType

class RoomConverters {
    @TypeConverter
    fun fromMovementType(value: MovementType): String {
        return value.name
    }

    @TypeConverter
    fun toMovementType(value: String): MovementType {
        return MovementType.valueOf(value)
    }
}
