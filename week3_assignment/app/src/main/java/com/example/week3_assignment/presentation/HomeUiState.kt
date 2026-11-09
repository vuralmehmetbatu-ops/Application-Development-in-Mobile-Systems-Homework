package com.example.week3_assignment.presentation

import com.example.week3_assignment.domain.DeviceCategory

data class DeviceUiModel(
    val id: String,
    val name: String,
    val category: DeviceCategory,
    val room: String,
    val isOnline: Boolean,
    val isActive: Boolean,
    val powerLabel: String,
    val statusLabel: String,
)

/** Everything the screen displays is prepared by the ViewModel. */
data class HomeUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedRoom: String? = null,
    val rooms: List<String> = emptyList(),
    val devices: List<DeviceUiModel> = emptyList(),
    val totalPowerWatts: Double = 0.0,
    val totalPowerLabel: String = "",
    val activeDevicesLabel: String = "",
    val onlineDevicesLabel: String = "",
    val visibleDevicesLabel: String = "",
)
