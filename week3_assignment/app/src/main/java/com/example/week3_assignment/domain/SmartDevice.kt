package com.example.week3_assignment.domain

enum class DeviceCategory {
    LIGHT,
    CAMERA,
    THERMOSTAT,
    VACUUM,
}

data class SmartDevice(
    val id: String,
    val name: String,
    val category: DeviceCategory,
    val room: String,
    val isOnline: Boolean,
    val isActive: Boolean,
    val powerWatts: Double,
)
