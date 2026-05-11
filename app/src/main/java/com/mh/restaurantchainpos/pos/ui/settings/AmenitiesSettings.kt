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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Accessible
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.House
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocalBar
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.LocalPizza
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.NightlightRound
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.RoomService
import androidx.compose.material.icons.outlined.SetMeal
import androidx.compose.material.icons.outlined.SmokingRooms
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.SportsBar
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ViewSidebar
import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WineBar
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

private data class AmenityOption(val id: String, val label: String, val icon: ImageVector)

private val AmenityList = listOf(
    AmenityOption("parking", "Parking", Icons.Outlined.LocalParking),
    AmenityOption("valet", "Valet", Icons.Outlined.DirectionsCar),
    AmenityOption("wifi", "Free WiFi", Icons.Outlined.Wifi),
    AmenityOption("credit-cards", "Credit Cards", Icons.Outlined.CreditCard),
    AmenityOption("cash", "Cash", Icons.Outlined.Payments),
    AmenityOption("mobile-pay", "Mobile Pay", Icons.Outlined.PhoneAndroid),
    AmenityOption("wheelchair", "Wheelchair", Icons.Outlined.Accessible),
    AmenityOption("high-chairs", "High Chairs", Icons.Outlined.ChildCare),
    AmenityOption("kids-menu", "Kids Menu", Icons.Outlined.Restaurant),
    AmenityOption("dog-friendly", "Dog Friendly", Icons.Outlined.Pets),
    AmenityOption("live-music", "Live Music", Icons.Outlined.MusicNote),
    AmenityOption("dress-code", "Dress Code", Icons.Outlined.Work),
    AmenityOption("smoking", "Smoking Area", Icons.Outlined.SmokingRooms),
    AmenityOption("private-events", "Private Events", Icons.Outlined.Celebration),
    AmenityOption("catering", "Catering", Icons.Outlined.RoomService),
    AmenityOption("delivery", "Delivery", Icons.Outlined.LocalShipping),
    AmenityOption("takeout", "Takeout", Icons.Outlined.LunchDining),
    AmenityOption("reservations", "Reservations", Icons.Outlined.AccessTime),
    AmenityOption("walk-ins", "Walk-ins", Icons.Outlined.Group),
    AmenityOption("outdoor", "Outdoor", Icons.Outlined.Weekend),
    AmenityOption("heated-patio", "Heated Patio", Icons.Outlined.DeviceThermostat),
    AmenityOption("ac", "A/C", Icons.Outlined.Air),
    AmenityOption("multilingual", "Multi-Lingual", Icons.Outlined.Language),
    AmenityOption("bar-lounge", "Bar / Lounge", Icons.Outlined.LocalBar),
)

private val CuisineList = listOf(
    AmenityOption("grilled-beef", "Grilled Beef", Icons.Outlined.LocalFireDepartment),
    AmenityOption("grilled-pork", "Grilled Pork", Icons.Outlined.DinnerDining),
    AmenityOption("bar-pub", "Bar & Pub", Icons.Outlined.SportsBar),
    AmenityOption("meat", "Meat", Icons.Outlined.RestaurantMenu),
    AmenityOption("fine-dining", "Fine Dining", Icons.Outlined.Star),
    AmenityOption("seafood", "Seafood", Icons.Outlined.SetMeal),
    AmenityOption("korean", "Korean", Icons.Outlined.LocalDining),
    AmenityOption("western", "Western Cuisine", Icons.Outlined.LocalPizza),
    AmenityOption("wine", "Wine", Icons.Outlined.WineBar),
    AmenityOption("brunch", "Brunch", Icons.Outlined.LocalCafe),
    AmenityOption("vegan", "Vegan", Icons.Outlined.Spa),
    AmenityOption("steakhouse", "Steakhouse", Icons.Outlined.RestaurantMenu),
    AmenityOption("fusion", "Fusion", Icons.Outlined.FlashOn),
    AmenityOption("healthy", "Healthy", Icons.Outlined.Grain),
    AmenityOption("noodles-soup", "Noodles & Soup", Icons.Outlined.RoomService),
    AmenityOption("family-meal", "Family Meal", Icons.Outlined.Group),
)

private val OccasionList = listOf(
    AmenityOption("date-night", "Date Night", Icons.Outlined.Favorite),
    AmenityOption("business-dinner", "Business Dinner", Icons.Outlined.Work),
    AmenityOption("celebration", "Celebration", Icons.Outlined.Cake),
    AmenityOption("casual-dining", "Casual Dining", Icons.Outlined.Restaurant),
    AmenityOption("romantic", "Romantic", Icons.Outlined.FavoriteBorder),
    AmenityOption("family-friendly", "Family-friendly", Icons.Outlined.ChildCare),
    AmenityOption("late-night", "Late Night", Icons.Outlined.NightlightRound),
    AmenityOption("quick-bite", "Quick Bite", Icons.Outlined.FlashOn),
)

private val SeatingList = listOf(
    AmenityOption("dining-hall", "Dining Hall", Icons.Outlined.House),
    AmenityOption("private-room", "Private Room", Icons.Outlined.MeetingRoom),
    AmenityOption("terrace", "Terrace", Icons.Outlined.Forest),
    AmenityOption("window-seat", "Window Seat", Icons.Outlined.ViewSidebar),
    AmenityOption("bar", "Bar", Icons.Outlined.LocalBar),
)

@Composable
fun AmenitiesSettings(colors: PosColors) {
    val amenities = remember { mutableStateListOf("parking", "wifi", "credit-cards", "cash", "mobile-pay", "high-chairs", "kids-menu", "reservations", "outdoor", "ac", "multilingual", "delivery", "takeout") }
    val cuisines = remember { mutableStateListOf("korean", "fusion", "wine") }
    val occasions = remember { mutableStateListOf("date-night", "celebration", "family-friendly") }
    val seating = remember { mutableStateListOf("dining-hall", "terrace", "bar") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AmenitiesGridCard(
            colors = colors,
            title = "Amenities & Services",
            subtitle = "Toggle features and services your restaurant offers",
            options = AmenityList,
            selected = amenities,
        )
        AmenitiesGridCard(
            colors = colors,
            title = "Cuisine",
            subtitle = "Select the cuisine types your restaurant serves",
            options = CuisineList,
            selected = cuisines,
        )
        AmenitiesGridCard(
            colors = colors,
            title = "Occasion & Vibe",
            subtitle = "Highlight the occasions and vibes your restaurant fits best",
            options = OccasionList,
            selected = occasions,
        )
        AmenitiesGridCard(
            colors = colors,
            title = "Seating Preference",
            subtitle = "Seating options available to your guests",
            options = SeatingList,
            selected = seating,
        )
    }
}

@Composable
private fun AmenitiesGridCard(
    colors: PosColors,
    title: String,
    subtitle: String,
    options: List<AmenityOption>,
    selected: SnapshotStateList<String>,
) {
    SettingCard(
        colors = colors,
        title = title,
        subtitle = subtitle,
        badge = "${selected.size} active",
        badgeIcon = null,
    ) {
        // Manually chunk into rows of 2 since we are inside a vertical-scroll container and
        // nested LazyVerticalGrid won't measure correctly. The screenshots use 2 columns on mobile.
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            options.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { option ->
                        AmenityTile(
                            colors = colors,
                            option = option,
                            active = selected.contains(option.id),
                            modifier = Modifier.weight(1f),
                        ) {
                            if (selected.contains(option.id)) selected.remove(option.id) else selected.add(option.id)
                        }
                    }
                    if (row.size == 1) Box(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AmenityTile(
    colors: PosColors,
    option: AmenityOption,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val borderColor = if (active) Blue600 else colors.border
    val background = if (active) Blue500.copy(alpha = 0.08f) else colors.surface
    val iconBg = if (active) Blue500.copy(alpha = 0.18f) else colors.surfaceRaised
    val iconTint = if (active) Blue600 else colors.textMuted
    val labelColor = if (active) Blue600 else colors.text

    Box(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        if (active) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Blue600),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
            }
        }
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(option.icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                option.label,
                color = labelColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
