package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

private val Amenities = listOf(
    "🅿 Parking", "🚗 Valet", "📶 Free WiFi", "💳 Credit Cards", "💵 Cash", "📱 Mobile Pay",
    "♿ Wheelchair", "👶 High Chairs", "🍴 Kids Menu", "🐶 Dog Friendly", "🎵 Live Music",
    "👔 Dress Code", "🚬 Smoking Area", "🎉 Private Events", "👨\u200d🍳 Catering", "🚚 Delivery",
    "🥡 Takeout", "📅 Reservations", "👥 Walk-ins", "🪑 Outdoor", "🌡 Heated Patio", "❄ A/C",
    "🌐 Multi-Lingual", "🍷 Bar / Lounge",
)

private val Cuisines = listOf(
    "🔥 Grilled Beef", "🍗 Grilled Pork", "🍺 Bar & Pub", "🥩 Meat", "💎 Fine Dining",
    "🐟 Seafood", "🍜 Korean", "🍴 Western", "🍷 Wine", "☕ Brunch", "🥗 Vegan",
    "🥩 Steakhouse", "✨ Fusion", "🥗 Healthy", "🥣 Noodles & Soup", "👨‍👩‍👧 Family Meal",
)

private val Occasions = listOf(
    "❤ Date Night", "💼 Business Dinner", "🎂 Celebration", "🍴 Casual Dining",
    "💕 Romantic", "👶 Family-friendly", "🌙 Late Night", "⚡ Quick Bite",
)

private val SeatingPrefs = listOf(
    "🏠 Dining Hall", "🚪 Private Room", "🌳 Terrace", "🪟 Window Seat", "🍷 Bar",
)

@Composable
fun AmenitiesSettings(colors: PosColors) {
    val amenities = remember { mutableStateListOf("📶 Free WiFi", "💳 Credit Cards", "💵 Cash", "📅 Reservations") }
    val cuisines = remember { mutableStateListOf("🍴 Western", "🥩 Steakhouse") }
    val occasions = remember { mutableStateListOf("❤ Date Night", "🎂 Celebration") }
    val seating = remember { mutableStateListOf("🏠 Dining Hall", "🌳 Terrace") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AmenityCard(colors, "Amenities", "Tap to enable.", Amenities, amenities)
        AmenityCard(colors, "Cuisine", "Categorize the kind of food you serve.", Cuisines, cuisines)
        AmenityCard(colors, "Occasion & vibe", "Help guests find you for the right moment.", Occasions, occasions)
        AmenityCard(colors, "Seating preference", "Inside, outside, private — pick all that apply.", SeatingPrefs, seating)
    }
}

@Composable
private fun AmenityCard(
    colors: PosColors,
    title: String,
    subtitle: String,
    options: List<String>,
    selected: androidx.compose.runtime.snapshots.SnapshotStateList<String>,
) {
    SettingCard(colors = colors, title = title, subtitle = subtitle, badge = "${selected.size}/${options.size}") {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(140.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(48.dp * ((options.size / 3) + 1)),
        ) {
            items(options) { option -> AmenityChip(colors, option, selected.contains(option)) {
                if (selected.contains(option)) selected.remove(option) else selected.add(option)
            } }
        }
    }
}

@Composable
private fun AmenityChip(colors: PosColors, label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) Blue500.copy(alpha = 0.15f) else colors.surfaceRaised)
            .border(1.dp, if (active) Blue500 else colors.border, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Text(label, color = if (active) Blue500 else colors.text, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
