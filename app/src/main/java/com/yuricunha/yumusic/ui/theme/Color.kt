package com.yuricunha.yumusic.ui.theme

import androidx.compose.ui.graphics.Color

// ── Background / Surface ───────────────────────────────────────────────
// TIDAL-inspired pure black OLED background with subtle warm greys
val Background = Color(0xFF000000)          // Pure black — OLED deep blacks
val BackgroundElevated = Color(0xFF0A0A0A)  // One step above pure black
val SurfaceCard = Color(0xFF141414)         // Card surfaces
val HoverRipple = Color(0xFF1C1C1C)        // Touch feedback / hover
val ActiveSelected = Color(0xFF242424)      // Selected state

// ── Text ───────────────────────────────────────────────────────────────
val TextPrimary = Color(0xFFF5F5F5)         // Near-white for max contrast
val TextSecondary = Color(0xFFB0B0B0)       // Grey for secondary info
val TextTertiary = Color(0xFF707070)        // Muted tertiary text

// ── Accent ─────────────────────────────────────────────────────────────
// Warm, sophisticated accent — like a subtle amber glow on HiFi gear
val PrimaryAccent = Color(0xFFD4A857)       // Warm gold — audiophile accent
val PrimaryAccentHover = Color(0xFFDEB666)  // Slightly brighter on hover
val AccentReadableOnDark = Color(0xFFE8C877)

// ── Dividers / borders ─────────────────────────────────────────────────
val Divider = Color(0x14F5F5F5)             // 8% white — barely there
val BorderCard = Color(0x0AF5F5F5)          // 4% white — extremely subtle
