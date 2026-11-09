package com.example.week3_assignment.presentation

import androidx.lifecycle.ViewModelStore
import com.example.week3_assignment.data.InMemoryDeviceRepository
import com.example.week3_assignment.data.SampleDevices
import com.example.week3_assignment.domain.SmartDevice
import com.example.week3_assignment.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val viewModelStore = ViewModelStore()

    @After
    fun clearViewModels() {
        viewModelStore.clear()
    }

    @Test
    fun `initial house power is 106 watts with six active and nine online devices`() = runTest {
        val viewModel = createAndCollectViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals(106.0, state.totalPowerWatts, 0.0001)
        assertEquals("106.0 W", state.totalPowerLabel)
        assertEquals("6 aktif cihaz", state.activeDevicesLabel)
        assertEquals("9/10 çevrimiçi", state.onlineDevicesLabel)
        assertEquals("10 cihaz", state.visibleDevicesLabel)
        assertEquals(10, state.devices.size)
        assertEquals(
            listOf("Salon", "Bahçe", "Antre", "Yatak Odası", "Mutfak", "Koridor", "Balkon", "Ofis"),
            state.rooms,
        )
    }

    @Test
    fun `toggles reactively update power to 91 then restore 106 and reach 151`() = runTest {
        val viewModel = createAndCollectViewModel()

        viewModel.onToggleDevice("1")
        runCurrent()
        assertEquals(91.0, viewModel.uiState.value.totalPowerWatts, 0.0001)
        assertEquals("Kapalı", viewModel.uiState.value.devices.first().statusLabel)

        viewModel.onToggleDevice("1")
        runCurrent()
        assertEquals(106.0, viewModel.uiState.value.totalPowerWatts, 0.0001)

        viewModel.onToggleDevice("7")
        runCurrent()
        assertEquals(151.0, viewModel.uiState.value.totalPowerWatts, 0.0001)

        viewModel.onToggleDevice("7")
        runCurrent()
        assertEquals(106.0, viewModel.uiState.value.totalPowerWatts, 0.0001)
    }

    @Test
    fun `search and room filters combine while total remains house wide`() = runTest {
        val viewModel = createAndCollectViewModel()

        viewModel.onSearchQueryChanged("lamba")
        viewModel.onRoomSelected("Salon")
        runCurrent()
        assertEquals(listOf("1"), viewModel.uiState.value.devices.map { it.id })
        assertEquals(106.0, viewModel.uiState.value.totalPowerWatts, 0.0001)

        viewModel.onRoomSelected("Ofis")
        runCurrent()
        assertEquals(listOf("10"), viewModel.uiState.value.devices.map { it.id })

        viewModel.onRoomSelected(null)
        runCurrent()
        assertEquals(listOf("1", "10"), viewModel.uiState.value.devices.map { it.id })

        viewModel.onSearchQueryChanged("")
        runCurrent()
        assertEquals(10, viewModel.uiState.value.devices.size)
    }

    @Test
    fun `room selection does not reduce total or available rooms`() = runTest {
        val viewModel = createAndCollectViewModel()
        val allRooms = viewModel.uiState.value.rooms

        viewModel.onRoomSelected("Salon")
        runCurrent()

        assertEquals(listOf("1", "2", "5"), viewModel.uiState.value.devices.map { it.id })
        assertEquals("Salon", viewModel.uiState.value.selectedRoom)
        assertEquals("3 cihaz", viewModel.uiState.value.visibleDevicesLabel)
        assertEquals(allRooms, viewModel.uiState.value.rooms)
        assertEquals(106.0, viewModel.uiState.value.totalPowerWatts, 0.0001)
    }

    @Test
    fun `Turkish search handles dotted I dotless I and surrounding whitespace`() = runTest {
        val viewModel = createAndCollectViewModel()

        viewModel.onSearchQueryChanged("  GİRİŞ  ")
        runCurrent()
        assertEquals(listOf("4"), viewModel.uiState.value.devices.map { it.id })
        assertEquals("  GİRİŞ  ", viewModel.uiState.value.searchQuery)

        viewModel.onSearchQueryChanged("ÇALIŞMA ODASI")
        runCurrent()
        assertEquals(listOf("10"), viewModel.uiState.value.devices.map { it.id })

        viewModel.onSearchQueryChanged("KÖŞE LAMBASI")
        runCurrent()
        assertEquals(listOf("1"), viewModel.uiState.value.devices.map { it.id })
    }

    @Test
    fun `empty search result preserves global power and counts`() = runTest {
        val viewModel = createAndCollectViewModel()

        viewModel.onSearchQueryChanged("olmayan cihaz")
        runCurrent()

        assertTrue(viewModel.uiState.value.devices.isEmpty())
        assertEquals("0 cihaz", viewModel.uiState.value.visibleDevicesLabel)
        assertEquals("106.0 W", viewModel.uiState.value.totalPowerLabel)
        assertEquals("6 aktif cihaz", viewModel.uiState.value.activeDevicesLabel)
    }

    @Test
    fun `toggle of hidden device still updates total while filters are active`() = runTest {
        val viewModel = createAndCollectViewModel()
        viewModel.onRoomSelected("Salon")
        runCurrent()

        viewModel.onToggleDevice("8")
        runCurrent()

        assertEquals(listOf("1", "2", "5"), viewModel.uiState.value.devices.map { it.id })
        assertEquals(56.0, viewModel.uiState.value.totalPowerWatts, 0.0001)
    }

    @Test
    fun `offline device contributes no power even when active`() = runTest {
        val initialDevices = SampleDevices.devices.map { device ->
            if (device.id == "9") device.copy(isActive = true) else device
        }
        val viewModel = createAndCollectViewModel(initialDevices)

        assertEquals(106.0, viewModel.uiState.value.totalPowerWatts, 0.0001)
        assertEquals("6 aktif cihaz", viewModel.uiState.value.activeDevicesLabel)
        val offline = viewModel.uiState.value.devices.first { it.id == "9" }
        assertEquals("Çevrimdışı", offline.statusLabel)
        assertEquals("25.0 W", offline.powerLabel)

        viewModel.onToggleDevice("9")
        runCurrent()
        assertTrue(viewModel.uiState.value.devices.first { it.id == "9" }.isActive)
        assertEquals(106.0, viewModel.uiState.value.totalPowerWatts, 0.0001)
    }

    @Test
    fun `offline and unknown actions do not change displayed state`() = runTest {
        val viewModel = createAndCollectViewModel()
        val original = viewModel.uiState.value

        viewModel.onToggleDevice("9")
        viewModel.onToggleDevice("missing")
        runCurrent()

        assertEquals(original, viewModel.uiState.value)
    }

    @Test
    fun `empty repository produces an empty valid screen`() = runTest {
        val viewModel = createAndCollectViewModel(emptyList())

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.devices.isEmpty())
        assertTrue(viewModel.uiState.value.rooms.isEmpty())
        assertEquals("0.0 W", viewModel.uiState.value.totalPowerLabel)
        assertEquals("0/0 çevrimiçi", viewModel.uiState.value.onlineDevicesLabel)
    }

    private fun TestScope.createAndCollectViewModel(
        initialDevices: List<SmartDevice> = SampleDevices.devices,
    ): HomeViewModel {
        val repository = InMemoryDeviceRepository(mainDispatcherRule.dispatcher, initialDevices)
        val viewModel = HomeViewModel(repository, mainDispatcherRule.dispatcher)
        viewModelStore.put("home", viewModel)
        // WhileSubscribed requires a real collector, as the Compose screen supplies.
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        runCurrent()
        return viewModel
    }
}
