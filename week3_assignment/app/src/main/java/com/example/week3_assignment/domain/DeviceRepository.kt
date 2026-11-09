package com.example.week3_assignment.domain

import kotlinx.coroutines.flow.StateFlow

interface DeviceRepository {
    val devices: StateFlow<List<SmartDevice>>

    suspend fun toggleDevice(id: String)
}
