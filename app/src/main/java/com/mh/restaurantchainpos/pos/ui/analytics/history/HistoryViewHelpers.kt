package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.ui.graphics.Color

internal fun kindIconAccent(kind: HistoryKind, isDark: Boolean): Pair<String, Color> = when (kind) {
    HistoryKind.Order -> "🧾" to (if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB))
    HistoryKind.Reservation -> "📅" to (if (isDark) Color(0xFF34D399) else Color(0xFF059669))
    HistoryKind.Payment -> "💳" to (if (isDark) Color(0xFFA78BFA) else Color(0xFF7C3AED))
    HistoryKind.NoShow -> "✕" to (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
    HistoryKind.WalkIn -> "👤" to (if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7))
}

internal fun statusBadgeBg(status: HistoryStatus, isDark: Boolean): Color = when (status) {
    HistoryStatus.Completed -> if (isDark) Color(0xFF064E3B) else Color(0xFFD1FAE5)
    HistoryStatus.Paid -> if (isDark) Color(0xFF1E3A8A) else Color(0xFFDBEAFE)
    HistoryStatus.NoShow -> if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0)
    HistoryStatus.Refunded -> if (isDark) Color(0xFF78350F) else Color(0xFFFEF3C7)
}

internal fun statusBadgeFg(status: HistoryStatus, isDark: Boolean): Color = when (status) {
    HistoryStatus.Completed -> if (isDark) Color(0xFF6EE7B7) else Color(0xFF047857)
    HistoryStatus.Paid -> if (isDark) Color(0xFF93C5FD) else Color(0xFF1D4ED8)
    HistoryStatus.NoShow -> if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
    HistoryStatus.Refunded -> if (isDark) Color(0xFFFCD34D) else Color(0xFFB45309)
}

internal fun formatLine(value: Double, currency: AnalyticsCurrency): String =
    when (currency) {
        AnalyticsCurrency.Domestic -> AnalyticsFormat.won(value.toLong())
        AnalyticsCurrency.Foreign -> AnalyticsFormat.usd(value)
    }
