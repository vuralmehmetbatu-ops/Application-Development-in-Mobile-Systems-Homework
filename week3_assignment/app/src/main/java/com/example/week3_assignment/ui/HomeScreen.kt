package com.example.week3_assignment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.week3_assignment.domain.DeviceCategory
import com.example.week3_assignment.presentation.DeviceUiModel
import com.example.week3_assignment.presentation.HomeUiState

/** The screen renders ViewModel state and forwards user events. */
@Composable
fun HomeScreen(
    state: HomeUiState,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onRoomSelected: (String?) -> Unit,
    onToggleDevice: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier.fillMaxSize(), color = Canvas, contentColor = Ink) {
        Box(
            Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyColumn(
                modifier = Modifier.widthIn(max = 720.dp).fillMaxSize().testTag("home_list"),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            ) {
                item(key = "header", contentType = "header") {
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 26.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            Modifier.size(44.dp).background(Panel, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Outlined.Home, contentDescription = null, tint = Accent, modifier = Modifier.size(26.dp))
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("Akıllı Ev", style = MaterialTheme.typography.headlineLarge)
                            Text("Kontrol paneli", color = Muted, fontSize = 12.sp)
                        }
                    }
                }
                item(key = "energy", contentType = "energy") {
                    EnergyPanel(state)
                    Spacer(Modifier.height(24.dp))
                }
                item(key = "search", contentType = "search") {
                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        modifier = Modifier.fillMaxWidth().testTag("search"),
                        placeholder = { Text("Cihaz ara", color = Muted, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Muted, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChanged("") }) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Aramayı temizle", modifier = Modifier.size(20.dp))
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Panel,
                            unfocusedContainerColor = Panel,
                            focusedTextColor = Ink,
                            unfocusedTextColor = Ink,
                            focusedIndicatorColor = Accent,
                            unfocusedIndicatorColor = Line,
                            cursorColor = Accent,
                        ),
                    )
                    Spacer(Modifier.height(12.dp))
                }
                item(key = "rooms", contentType = "rooms") {
                    LazyRow(modifier = Modifier.testTag("room_tabs")) {
                        item(key = "all") {
                            RoomTab("Tüm odalar", state.selectedRoom == null, "room_all") { onRoomSelected(null) }
                        }
                        items(state.rooms, key = { it }) { room ->
                            RoomTab(room, state.selectedRoom == room, "room_$room") { onRoomSelected(room) }
                        }
                    }
                    HorizontalDivider(color = Line)
                    Spacer(Modifier.height(24.dp))
                }
                item(key = "devices_heading", contentType = "heading") {
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Cihazlar", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                        Text(state.visibleDevicesLabel, color = Muted, fontFamily = FontFamily.Monospace, fontSize = 12.sp, modifier = Modifier.testTag("device_count"))
                    }
                }
                if (state.isLoading) {
                    item(key = "loading") {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(Modifier.size(26.dp), strokeWidth = 2.dp)
                        }
                    }
                } else if (state.devices.isEmpty()) {
                    item(key = "empty") {
                        Column(
                            Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(6.dp)).padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text("Cihaz bulunamadı", style = MaterialTheme.typography.titleMedium)
                            Text("Aramayı veya seçili odayı değiştir.", color = Muted, style = MaterialTheme.typography.bodyMedium)
                            TextButton(onClick = {
                                onSearchQueryChanged("")
                                onRoomSelected(null)
                            }, contentPadding = PaddingValues(horizontal = 0.dp)) {
                                Text("Filtreleri temizle")
                            }
                        }
                    }
                } else {
                    items(state.devices, key = { it.id }, contentType = { "device" }) { device ->
                        DeviceRow(device = device, onToggle = { onToggleDevice(device.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun EnergyPanel(state: HomeUiState) {
    Surface(shape = RoundedCornerShape(8.dp), color = Panel, contentColor = Ink, modifier = Modifier.fillMaxWidth()) {
        Column {
            Box(Modifier.fillMaxWidth().height(3.dp).background(Accent))
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Anlık tüketim", modifier = Modifier.weight(1f), fontSize = 14.sp, color = Muted)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.size(6.dp).background(Accent, CircleShape))
                        Text("CANLI", color = Accent, fontSize = 10.sp, letterSpacing = 1.sp)
                    }
                }
                if (state.isLoading) {
                    Text("Yükleniyor…", color = Ink, fontSize = 30.sp, modifier = Modifier.padding(vertical = 6.dp))
                } else {
                    Text(
                        state.totalPowerLabel,
                        color = Ink,
                        fontSize = 42.sp,
                        lineHeight = 48.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = (-2).sp,
                        modifier = Modifier.testTag("total_power"),
                    )
                }
                HorizontalDivider(color = Line)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(state.activeDevicesLabel, color = Muted, fontSize = 12.sp)
                    Text(state.onlineDevicesLabel, color = Muted, fontSize = 12.sp)
                }
                Text("Tüm ev · Açık ve çevrimiçi cihazlar", color = Muted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun RoomTab(label: String, selected: Boolean, tag: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(IntrinsicSize.Max).widthIn(min = 76.dp)
            .selectable(selected = selected, onClick = onClick, role = Role.Tab).testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 15.dp),
            color = if (selected) Accent else Muted,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
        Box(Modifier.fillMaxWidth().height(2.dp).background(if (selected) Accent else Color.Transparent))
    }
}

@Composable
private fun DeviceRow(device: DeviceUiModel, onToggle: () -> Unit) {
    Surface(color = Panel, contentColor = Ink, modifier = Modifier.fillMaxWidth().testTag("device_${device.id}")) {
        Column {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        Modifier.size(38.dp).background(Canvas, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            categoryIcon(device.category),
                            contentDescription = null,
                            modifier = Modifier.size(21.dp),
                            tint = if (device.isOnline && device.isActive) Accent else Muted,
                        )
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(device.name, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(device.room, color = Muted, fontSize = 12.sp)
                    }
                    Text(device.powerLabel, color = Ink, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(5.dp).background(
                        if (!device.isOnline) Amber else if (device.isActive) Accent else Muted, CircleShape,
                    ))
                    Text(device.statusLabel, modifier = Modifier.weight(1f).padding(start = 7.dp), color = if (!device.isOnline) Amber else Muted, fontSize = 12.sp)
                    Button(
                        onClick = onToggle,
                        enabled = device.isOnline,
                        modifier = Modifier.heightIn(min = 48.dp).testTag("toggle_${device.id}").semantics {
                            contentDescription = device.name
                        },
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (device.isActive) Color(0xFF253D60) else Color(0xFF273345),
                            contentColor = if (device.isActive) Accent else Ink,
                            disabledContainerColor = Canvas,
                            disabledContentColor = Muted,
                        ),
                    ) {
                        Icon(Icons.Outlined.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(15.dp))
                        Text(if (device.isActive) "Kapat" else "Aç", modifier = Modifier.padding(start = 6.dp), fontSize = 12.sp)
                    }
                }
            }
            HorizontalDivider(color = Line, modifier = Modifier.padding(horizontal = 14.dp))
        }
    }
}

private fun categoryIcon(category: DeviceCategory): ImageVector = when (category) {
    DeviceCategory.LIGHT -> Icons.Outlined.Lightbulb
    DeviceCategory.CAMERA -> Icons.Outlined.Videocam
    DeviceCategory.THERMOSTAT -> Icons.Outlined.Thermostat
    DeviceCategory.VACUUM -> Icons.Outlined.SmartToy
}
