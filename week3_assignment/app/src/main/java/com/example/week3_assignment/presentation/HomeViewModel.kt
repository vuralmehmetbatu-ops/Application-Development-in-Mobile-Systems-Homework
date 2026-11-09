package com.example.week3_assignment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.week3_assignment.domain.DeviceRepository
import com.example.week3_assignment.domain.SmartDevice
import java.util.Locale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: DeviceRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val mutableSearchQuery = MutableStateFlow("")
    // Text input must echo synchronously. A background-derived value can drop
    // rapid keystrokes when TextField receives an older value after an edit.
    val searchQuery: StateFlow<String> = mutableSearchQuery.asStateFlow()
    private val selectedRoom = MutableStateFlow<String?>(null)
    private val turkishLocale = Locale.forLanguageTag("tr-TR")

    val uiState: StateFlow<HomeUiState> = combine(
        repository.devices,
        searchQuery,
        selectedRoom,
    ) { allDevices, query, room ->
        val normalizedQuery = query.trim().lowercase(turkishLocale)
        val visibleDevices = allDevices.filter { device ->
            (room == null || device.room == room) &&
                device.name.lowercase(turkishLocale).contains(normalizedQuery)
        }
        // The house total always uses the entire data set, regardless of filters.
        val consumingDevices = allDevices.filter { it.isOnline && it.isActive }
        val totalPower = consumingDevices.sumOf { it.powerWatts }

        HomeUiState(
            isLoading = false,
            searchQuery = query,
            selectedRoom = room,
            rooms = allDevices.map { it.room }.distinct(),
            devices = visibleDevices.map { it.toUiModel() },
            totalPowerWatts = totalPower,
            totalPowerLabel = formatPower(totalPower),
            activeDevicesLabel = "${consumingDevices.size} aktif cihaz",
            onlineDevicesLabel = "${allDevices.count { it.isOnline }}/${allDevices.size} çevrimiçi",
            visibleDevicesLabel = "${visibleDevices.size} cihaz",
        )
    }
        // Filtering, sums, and all display formatting run off the main thread.
        .flowOn(defaultDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = HomeUiState(),
        )

    fun onSearchQueryChanged(query: String) {
        mutableSearchQuery.value = query
    }

    fun onRoomSelected(room: String?) {
        selectedRoom.value = room
    }

    fun onToggleDevice(id: String) {
        viewModelScope.launch(defaultDispatcher) {
            repository.toggleDevice(id)
        }
    }

    private fun SmartDevice.toUiModel() = DeviceUiModel(
        id = id,
        name = name,
        category = category,
        room = room,
        isOnline = isOnline,
        isActive = isActive,
        powerLabel = formatPower(powerWatts),
        statusLabel = when {
            !isOnline -> "Çevrimdışı"
            isActive -> "Açık"
            else -> "Kapalı"
        },
    )

    private fun formatPower(watts: Double): String = String.format(Locale.US, "%.1f W", watts)
}
