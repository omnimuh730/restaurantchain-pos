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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

private data class AmenityOption(val id: String, val icon: ImageVector)

@Composable
private fun amenityLabel(id: String): String {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val resName = "amenity_" + id.replace('-', '_')
    val resId = remember(id, configuration) {
        context.resources.getIdentifier(resName, "string", context.packageName)
    }
    return if (resId != 0) context.resources.getString(resId) else id
}

private val AmenityList = listOf(
    AmenityOption("parking", Icons.Outlined.LocalParking),
    AmenityOption("valet", Icons.Outlined.DirectionsCar),
    AmenityOption("wifi", Icons.Outlined.Wifi),
    AmenityOption("credit-cards", Icons.Outlined.CreditCard),
    AmenityOption("cash", Icons.Outlined.Payments),
    AmenityOption("mobile-pay", Icons.Outlined.PhoneAndroid),
    AmenityOption("wheelchair", Icons.Outlined.Accessible),
    AmenityOption("high-chairs", Icons.Outlined.ChildCare),
    AmenityOption("kids-menu", Icons.Outlined.Restaurant),
    AmenityOption("dog-friendly", Icons.Outlined.Pets),
    AmenityOption("live-music", Icons.Outlined.MusicNote),
    AmenityOption("dress-code", Icons.Outlined.Work),
    AmenityOption("smoking", Icons.Outlined.SmokingRooms),
    AmenityOption("private-events", Icons.Outlined.Celebration),
    AmenityOption("catering", Icons.Outlined.RoomService),
    AmenityOption("delivery", Icons.Outlined.LocalShipping),
    AmenityOption("takeout", Icons.Outlined.LunchDining),
    AmenityOption("reservations", Icons.Outlined.AccessTime),
    AmenityOption("walk-ins", Icons.Outlined.Group),
    AmenityOption("outdoor", Icons.Outlined.Weekend),
    AmenityOption("heated-patio", Icons.Outlined.DeviceThermostat),
    AmenityOption("ac", Icons.Outlined.Air),
    AmenityOption("multilingual", Icons.Outlined.Language),
    AmenityOption("bar-lounge", Icons.Outlined.LocalBar),
)

private val CuisineList = listOf(
    AmenityOption("grilled-beef", Icons.Outlined.LocalFireDepartment),
    AmenityOption("grilled-pork", Icons.Outlined.DinnerDining),
    AmenityOption("bar-pub", Icons.Outlined.SportsBar),
    AmenityOption("meat", Icons.Outlined.RestaurantMenu),
    AmenityOption("fine-dining", Icons.Outlined.Star),
    AmenityOption("seafood", Icons.Outlined.SetMeal),
    AmenityOption("korean", Icons.Outlined.LocalDining),
    AmenityOption("western", Icons.Outlined.LocalPizza),
    AmenityOption("wine", Icons.Outlined.WineBar),
    AmenityOption("brunch", Icons.Outlined.LocalCafe),
    AmenityOption("vegan", Icons.Outlined.Spa),
    AmenityOption("steakhouse", Icons.Outlined.RestaurantMenu),
    AmenityOption("fusion", Icons.Outlined.FlashOn),
    AmenityOption("healthy", Icons.Outlined.Grain),
    AmenityOption("noodles-soup", Icons.Outlined.RoomService),
    AmenityOption("family-meal", Icons.Outlined.Group),
)

private val OccasionList = listOf(
    AmenityOption("date-night", Icons.Outlined.Favorite),
    AmenityOption("business-dinner", Icons.Outlined.Work),
    AmenityOption("celebration", Icons.Outlined.Cake),
    AmenityOption("casual-dining", Icons.Outlined.Restaurant),
    AmenityOption("romantic", Icons.Outlined.FavoriteBorder),
    AmenityOption("family-friendly", Icons.Outlined.ChildCare),
    AmenityOption("late-night", Icons.Outlined.NightlightRound),
    AmenityOption("quick-bite", Icons.Outlined.FlashOn),
)

private val SeatingList = listOf(
    AmenityOption("dining-hall", Icons.Outlined.House),
    AmenityOption("private-room", Icons.Outlined.MeetingRoom),
    AmenityOption("terrace", Icons.Outlined.Forest),
    AmenityOption("window-seat", Icons.Outlined.ViewSidebar),
    AmenityOption("bar", Icons.Outlined.LocalBar),
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
            titleRes = R.string.settings_amenities_services_title,
            subtitleRes = R.string.settings_amenities_services_sub,
            options = AmenityList,
            selected = amenities,
        )
        AmenitiesGridCard(
            colors = colors,
            titleRes = R.string.settings_amenities_cuisine_title,
            subtitleRes = R.string.settings_amenities_cuisine_sub,
            options = CuisineList,
            selected = cuisines,
        )
        AmenitiesGridCard(
            colors = colors,
            titleRes = R.string.settings_amenities_occasion_title,
            subtitleRes = R.string.settings_amenities_occasion_sub,
            options = OccasionList,
            selected = occasions,
        )
        AmenitiesGridCard(
            colors = colors,
            titleRes = R.string.settings_amenities_seating_title,
            subtitleRes = R.string.settings_amenities_seating_sub,
            options = SeatingList,
            selected = seating,
        )
    }
}

@Composable
private fun AmenitiesGridCard(
    colors: PosColors,
    titleRes: Int,
    subtitleRes: Int,
    options: List<AmenityOption>,
    selected: SnapshotStateList<String>,
) {
    SettingCard(
        colors = colors,
        title = stringResource(titleRes),
        subtitle = stringResource(subtitleRes),
        badge = stringResource(R.string.settings_amenities_active_count, selected.size),
        badgeIcon = null,
    ) {
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
    val label = amenityLabel(option.id)

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
                label,
                color = labelColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
