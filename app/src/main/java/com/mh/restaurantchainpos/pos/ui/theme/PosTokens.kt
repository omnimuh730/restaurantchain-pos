package com.mh.restaurantchainpos.pos.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class PosColors(
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val headerBorder: Color,
    val navBackground: Color,
    val navInactive: Color,
    val text: Color,
    val textMuted: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val border: Color,
    val chip: Color,
    val overlay: Color,
)

val LightPosColors = PosColors(
    backgroundTop = Color(0xFFE2E8F0),
    backgroundBottom = Color(0xFFF8FAFC),
    headerBorder = Color(0xFFCBD5E1),
    navBackground = Color.White,
    navInactive = Color(0xFF94A3B8),
    text = Color(0xFF1E293B),
    textMuted = Color(0xFF64748B),
    surface = Color.White,
    surfaceRaised = Color(0xFFF1F5F9),
    border = Color(0xFFE2E8F0),
    chip = Color(0xFFF3F4F6),
    overlay = Color(0x66000000),
)

val DarkPosColors = PosColors(
    backgroundTop = Color(0xFF141A22),
    backgroundBottom = Color(0xFF0B0F14),
    headerBorder = Color(0xFF222C38),
    navBackground = Color(0xFF0B0F14),
    navInactive = Color(0xFF4A5463),
    text = Color(0xFFE5E7EB),
    textMuted = Color(0xFF9CA3AF),
    surface = Color(0xFF2A2D35),
    surfaceRaised = Color(0xFF3A3F4D),
    border = Color(0xFF374151),
    chip = Color(0xFF3A3F4D),
    overlay = Color(0x99000000),
)

object PosDimens {
    val HeaderHeight = 64.dp
    val BottomNavHeight = 80.dp
    val RadiusSm = 8.dp
    val RadiusMd = 10.dp
    val RadiusLg = 12.dp
    val RadiusXl = 16.dp
    val SpaceXs = 4.dp
    val SpaceSm = 8.dp
    val SpaceMd = 12.dp
    val SpaceLg = 16.dp
    val SpaceXl = 20.dp
}

fun posBackground(colors: PosColors): Brush =
    Brush.verticalGradient(listOf(colors.backgroundTop, colors.backgroundBottom))

val Blue600 = Color(0xFF2563EB)
val Blue500 = Color(0xFF3B82F6)
val Blue400 = Color(0xFF60A5FA)
val Green500 = Color(0xFF22C55E)
val Green400 = Color(0xFF4ADE80)
val Amber500 = Color(0xFFF59E0B)
val Amber400 = Color(0xFFFBBF24)
val Orange500 = Color(0xFFF97316)
val Red500 = Color(0xFFEF4444)
val Red400 = Color(0xFFF87171)
val Slate400 = Color(0xFF94A3B8)
val Slate300 = Color(0xFFCBD5E1)
val Slate200 = Color(0xFFE2E8F0)

data class FloorPalette(
    val bg: Color,
    val card: Color,
    val raised: Color,
    val border: Color,
    val text1: Color,
    val text2: Color,
    val text3: Color,
    val availableFill: Color,
    val availableBorder: Color,
    val availableText: Color,
    val occupiedFill: Color,
    val occupiedBorder: Color,
    val occupiedText: Color,
    val reservedFill: Color,
    val reservedBorder: Color,
    val reservedText: Color,
    val primary: Color,
    val amber: Color,
    val editBg: Color,
    val editCanvas: Color,
    val editBorder: Color,
    val editSelected: Color,
    val editTableDefault: Color,
    val editText1: Color,
    val editText2: Color,
    val editText3: Color,
)

val LightFloorPalette = FloorPalette(
    bg = Color.Transparent,
    card = Color.White,
    raised = Color(0xFFF8FAFC),
    border = Color(0xFFE2E8F0),
    text1 = Color(0xFF1E293B),
    text2 = Color(0xFF64748B),
    text3 = Color(0xFF94A3B8),
    availableFill = Color(0xFFF1F5F9),
    availableBorder = Color(0xFFCBD5E1),
    availableText = Color(0xFF94A3B8),
    occupiedFill = Color(0x1A3B82F6),
    occupiedBorder = Color(0xFF3B82F6),
    occupiedText = Color(0xFF2563EB),
    reservedFill = Color(0x1AF59E0B),
    reservedBorder = Color(0xFFF59E0B),
    reservedText = Color(0xFFD97706),
    primary = Color(0xFF3B82F6),
    amber = Color(0xFFF59E0B),
    editBg = Color(0xFFF5F6FA),
    editCanvas = Color(0xFFFFFFFF),
    editBorder = Color(0xFFE0E3EA),
    editSelected = Color(0xFF4B83FF),
    editTableDefault = Color(0xFFD4D8E0),
    editText1 = Color(0xFF1A1D26),
    editText2 = Color(0xFF6B7280),
    editText3 = Color(0xFF9CA3AF),
)

val DarkFloorPalette = FloorPalette(
    bg = Color.Transparent,
    card = Color(0xFF141A22),
    raised = Color(0xFF1C242F),
    border = Color(0xFF222C38),
    text1 = Color(0xFFE8EDF2),
    text2 = Color(0xFF8A96A6),
    text3 = Color(0xFF4A5463),
    availableFill = Color(0xFF2A3441),
    availableBorder = Color(0xFF3A4656),
    availableText = Color(0xFF5A6778),
    occupiedFill = Color(0x262B6CFF),
    occupiedBorder = Color(0xFF4A8BFF),
    occupiedText = Color(0xFF7BA7FF),
    reservedFill = Color(0x26D98A2B),
    reservedBorder = Color(0xFFE0A355),
    reservedText = Color(0xFFF0B870),
    primary = Color(0xFF2B6CFF),
    amber = Color(0xFFD98A2B),
    editBg = Color(0xFF0F1318),
    editCanvas = Color(0xFF161B22),
    editBorder = Color(0xFF2A3441),
    editSelected = Color(0xFF4B83FF),
    editTableDefault = Color(0xFF2A3441),
    editText1 = Color(0xFFE8EDF2),
    editText2 = Color(0xFF8A96A6),
    editText3 = Color(0xFF4A5463),
)
