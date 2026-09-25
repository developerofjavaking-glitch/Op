package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Classic Light Mode Canvas & Surfaces
val ClassicBg = Color(0xFFF8FAFC)              // Soft warm light slate background
val ClassicSurface = Color(0xFFFFFFFF)         // Clean pure white surface
val ClassicSurfaceVariant = Color(0xFFF1F5F9)  // Secondary surface for inputs & chips
val ClassicBorder = Color(0xFFE2E8F0)          // Soft clean border
val ClassicBorderLight = Color(0xFFCBD5E1)     // Crisp stroke border

// Classic Brand Primaries & Semantic Accents
val ClassicBlue = Color(0xFF1A56DB)            // Timeless royal sapphire blue (primary)
val ClassicBlueHover = Color(0xFF1E429F)
val ClassicBlueContainer = Color(0xFFEBF5FF)   // Soft friendly container
val ClassicBlueText = Color(0xFF1E40AF)

val ClassicGreen = Color(0xFF059669)           // Forest green (Wi-Fi, dial, success)
val ClassicGreenContainer = Color(0xFFDEF7EC)
val ClassicGreenText = Color(0xFF03543F)

val ClassicPurple = Color(0xFF7E3AF2)          // Royal purple (contacts)
val ClassicPurpleContainer = Color(0xFFEDEBFE)

val ClassicAmber = Color(0xFFD97706)           // Warm gold/amber (bookmarks, favorites)
val ClassicAmberContainer = Color(0xFFFEF3C7)

val ClassicRed = Color(0xFFE02424)             // Classic crimson red (delete, clear)
val ClassicRedContainer = Color(0xFFFDE8E8)

// Text Colors (High Contrast for all ages - Senior & Kid Friendly)
val TextDarkPrimary = Color(0xFF0F172A)        // Deep slate charcoal (high contrast)
val TextDarkSecondary = Color(0xFF334155)      // Rich slate secondary
val TextDarkMuted = Color(0xFF64748B)          // Muted labels and hints
val TextLight = Color(0xFFFFFFFF)              // For dark buttons

// Reticle & Scanner Overlay (over live camera preview)
val OverlayScrim = Color(0x99000000)
val ViewfinderReticle = Color(0xFF1A56DB)
val LaserBeam = Color(0xFF2563EB)
val LaserBeamGlow = Color(0x663B82F6)

// Compatibility aliases for legacy references if any
val ScannerDarkBg = ClassicBg
val ScannerSurface = ClassicSurface
val ScannerSurfaceVariant = ClassicSurfaceVariant
val ScannerBorder = ClassicBorder
val ScannerBorderLight = ClassicBorderLight
val NeonCyan = ClassicBlue
val TextWhite = TextLight
val TextMuted = TextDarkMuted
val TextSubtle = TextDarkSecondary
val EmeraldGreen = ClassicGreen
val ElectricViolet = ClassicPurple
val NeonPurple = ClassicPurple
val LaserColor = LaserBeam
val LaserGlow = LaserBeamGlow
