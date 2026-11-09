package com.example.week3_assignment.data

import com.example.week3_assignment.domain.DeviceCategory
import com.example.week3_assignment.domain.SmartDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryDeviceRepositoryTest {
    @Test
    fun `initial data exactly matches all ten assignment records`() {
        val expected = listOf(
            SmartDevice("1", "Köşe Lambası", DeviceCategory.LIGHT, "Salon", true, true, 15.0),
            SmartDevice("2", "Tavan Avizesi", DeviceCategory.LIGHT, "Salon", true, false, 60.0),
            SmartDevice("3", "Bahçe Kamerası", DeviceCategory.CAMERA, "Bahçe", true, true, 8.5),
            SmartDevice("4", "Giriş Kamerası", DeviceCategory.CAMERA, "Antre", true, true, 8.5),
            SmartDevice("5", "Merkezi Termostat", DeviceCategory.THERMOSTAT, "Salon", true, true, 12.0),
            SmartDevice("6", "Yatak Odası Kliması", DeviceCategory.THERMOSTAT, "Yatak Odası", true, false, 1200.0),
            SmartDevice("7", "Mutfak Spotları", DeviceCategory.LIGHT, "Mutfak", true, false, 45.0),
            SmartDevice("8", "Roborock S7", DeviceCategory.VACUUM, "Koridor", true, true, 50.0),
            SmartDevice("9", "Balkon Aydınlatması", DeviceCategory.LIGHT, "Balkon", false, false, 25.0),
            SmartDevice("10", "Çalışma Odası Lambası", DeviceCategory.LIGHT, "Ofis", true, true, 12.0),
        )

        assertEquals(expected, InMemoryDeviceRepository().devices.value)
    }

    @Test
    fun `online toggle changes only its target and preserves original snapshot`() = runTest {
        val repository = InMemoryDeviceRepository(StandardTestDispatcher(testScheduler))
        val original = repository.devices.value

        repository.toggleDevice("1")

        assertTrue(original.first().isActive)
        assertFalse(repository.devices.value.first().isActive)
        assertEquals(original.drop(1), repository.devices.value.drop(1))
        repository.toggleDevice("1")
        assertEquals(original, repository.devices.value)
    }

    @Test
    fun `offline and unknown device actions leave data unchanged`() = runTest {
        val repository = InMemoryDeviceRepository(StandardTestDispatcher(testScheduler))
        val original = repository.devices.value

        repository.toggleDevice("9")
        repository.toggleDevice("unknown")

        assertEquals(original, repository.devices.value)
    }

    @Test
    fun `concurrent toggles do not lose updates`() = runTest {
        val repository = InMemoryDeviceRepository(Dispatchers.Default)

        // Real worker threads exercise the StateFlow compare-and-set update path.
        List(2_001) {
            launch(Dispatchers.Default) { repository.toggleDevice("1") }
        }.joinAll()
        assertFalse(repository.devices.value.first { it.id == "1" }.isActive)

        List(2_000) {
            launch(Dispatchers.Default) { repository.toggleDevice("2") }
        }.joinAll()
        assertFalse(repository.devices.value.first { it.id == "2" }.isActive)
    }
}
