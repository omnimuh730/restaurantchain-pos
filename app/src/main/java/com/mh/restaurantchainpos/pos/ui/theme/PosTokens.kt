package com.mh.restaurantchainpos.pos.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val Blue600 = Color(0xFF2563EB)
val Blue500 = Color(0xFF3B82F6)
val Blue700 = Color(0xFF1D4ED8)
val Blue400 = Color(0xFF60A5FA)
val Blue300 = Color(0xFF93C5FD)
val Blue50 = Color(0xFFEFF6FF)
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

data class PosColors(
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val headerBorder: Color,
    val navBackground: Color,
    val navInactive: Color,
    /** Bottom / rail icon + label when the tab is selected (original: near-black on light). */
    val navActive: Color,
    val text: Color,
    val textMuted: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val border: Color,
    /** Search fields and inputs: stroke that stays visible on `surfaceRaised` (esp. dark theme). */
    val inputOutline: Color,
    val chip: Color,
    val overlay: Color,
    /**
     * Tinted background used to highlight the "NEW ITEMS" section so it stands out
     * from the surrounding order list. Light: subtle Blue-50; dark: dark blue tint.
     */
    val newItemsBg: Color,
    // —— Semantic interactive / brand (theme-aware; fixes low-contrast “active” in dark mode) ——
    /** Primary brand accent: selected nav icon/label, key links, filled control strokes. */
    val accent: Color,
    /** Text/icons on a solid [accent] fill (dropdown row selected, badges on accent). */
    val onAccent: Color,
    /** Soft accent surface (dropdown triggers, chips on neutral). */
    val accentContainer: Color,
    /** Text on [accentContainer] (must meet contrast vs accentContainer). */
    val onAccentContainer: Color,
    /** Bottom nav / rail: selected tab background (must read on [navBackground]). */
    val navSelectedBackground: Color,
    /** Bottom nav: top indicator sliver. */
    val navSelectedIndicator: Color,
    /** Bottom nav / rail: icon + label when selected (WCAG vs navBackground). */
    val navSelectedForeground: Color,
)

val LightPosColors = PosColors(
    /** Darker scaffold so white/surface cards read with stronger separation. */
    backgroundTop = Color(0xFFD9E1EB),
    backgroundBottom = Color(0xFFD9E1EB),
    headerBorder = Color(0xFFCBD5E1),
    navBackground = Color.White,
    navInactive = Color(0xFF94A3B8),
    navActive = Color(0xFF111827),
    text = Color(0xFF1E293B),
    textMuted = Color(0xFF64748B),
    surface = Color.White,
    surfaceRaised = Color(0xFFEEF2F7),
    border = Color(0xFFE2E8F0),
    inputOutline = Color(0xFFCBD5E1),
    /** Foreground "tile" surface for category buttons / food tiles - lighter than the panel bg. */
    chip = Color.White,
    overlay = Color(0x66000000),
    newItemsBg = Color(0xFFEFF6FF),
    accent = Blue600,
    onAccent = Color.White,
    accentContainer = Blue50,
    onAccentContainer = Blue700,
    navSelectedBackground = Blue600.copy(alpha = PosOpacity.NavSelectedLight),
    navSelectedIndicator = Blue600,
    navSelectedForeground = Blue600,
)

val DarkPosColors = PosColors(
    backgroundTop = Color(0xFF141A22),
    backgroundBottom = Color(0xFF0B0F14),
    headerBorder = Color(0xFF222C38),
    navBackground = Color(0xFF0B0F14),
    navInactive = Color(0xFF4A5463),
    navActive = Color(0xFFF3F4F6),
    text = Color(0xFFE5E7EB),
    textMuted = Color(0xFF9CA3AF),
    surface = Color(0xFF2A2D35),
    surfaceRaised = Color(0xFF3A3F4D),
    border = Color(0xFF374151),
    inputOutline = Color(0xFF6B7280),
    /** Foreground "tile" surface for category buttons / food tiles - lighter than surfaceRaised. */
    chip = Color(0xFF454A58),
    overlay = Color(0x99000000),
    newItemsBg = Color(0xFF2C3550),
    // Brighter accent on dark surfaces so tabs, menus, and badges stay legible.
    accent = Blue400,
    onAccent = Color(0xFF0B1220),
    accentContainer = Color(0xFF243656),
    onAccentContainer = Blue300,
    navSelectedBackground = Blue500.copy(alpha = PosOpacity.NavSelectedDark),
    navSelectedIndicator = Blue400,
    /** Active tab icon + label: bright white on tinted blue chip (reads clearly in dark). */
    navSelectedForeground = Color(0xFFF9FAFB),
)

object PosDimens {
    val HeaderHeight = 64.dp
    val BottomNavHeight = 80.dp
    /** Start-edge navigation rail width in the wide shell layout. */
    val NavigationRailWidth = 76.dp
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
    /** Solid canvas so dot grid reads; darker than table cards for contrast. */
    bg = Color(0xFFD9E1EB),
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
    bg = Color(0xFF0A0D12),
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
