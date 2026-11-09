package com.example.week3_assignment.data

import com.example.week3_assignment.domain.DeviceRepository
import com.example.week3_assignment.domain.SmartDevice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class InMemoryDeviceRepository(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    initialDevices: List<SmartDevice> = SampleDevices.devices,
) : DeviceRepository {
    private val mutableDevices = MutableStateFlow(initialDevices.toList())
    override val devices: StateFlow<List<SmartDevice>> = mutableDevices.asStateFlow()

    override suspend fun toggleDevice(id: String) = withContext(defaultDispatcher) {
        // update is atomic: simultaneous taps cannot overwrite one another.
        mutableDevices.update { currentDevices ->
            val target = currentDevices.firstOrNull { it.id == id }
            if (target == null || !target.isOnline) {
                currentDevices
            } else {
                currentDevices.map { device ->
                    if (device.id == id) device.copy(isActive = !device.isActive) else device
                }
            }
        }
    }
}
