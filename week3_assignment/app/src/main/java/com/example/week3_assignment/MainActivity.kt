package com.example.week3_assignment

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.week3_assignment.data.InMemoryDeviceRepository
import com.example.week3_assignment.presentation.HomeViewModel
import com.example.week3_assignment.ui.HomeScreen
import com.example.week3_assignment.ui.SmartHomeTheme

class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(HomeViewModel::class.java))
                return HomeViewModel(InMemoryDeviceRepository()) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            SmartHomeTheme {
                // The route observes lifecycle-aware state. HomeScreen only renders it.
                val state by homeViewModel.uiState.collectAsStateWithLifecycle()
                val searchQuery by homeViewModel.searchQuery.collectAsStateWithLifecycle()
                HomeScreen(
                    state = state,
                    searchQuery = searchQuery,
                    onSearchQueryChanged = homeViewModel::onSearchQueryChanged,
                    onRoomSelected = homeViewModel::onRoomSelected,
                    onToggleDevice = homeViewModel::onToggleDevice,
                )
            }
        }
    }
}
