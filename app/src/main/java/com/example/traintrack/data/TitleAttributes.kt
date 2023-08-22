package com.example.traintrack.data

import androidx.compose.ui.graphics.Color

val nameToAbbreviation = mapOf<String, String>(
    "Barrie" to "BR",
    "Kitchener" to "KI",
    "Lakeshore East" to "LE",
    "Lakeshore West" to "LW",
    "Milton" to "MI",
    "Richmond Hill" to "RH",
    "Stouffville" to "ST",
)

val nameToColor = mapOf<String, Color>(
    "Barrie" to Color(0xFF005F9F),
    "Kitchener" to Color(0xFF00863E),
    "Lakeshore East" to Color(0xFFEF3C33),
    "Lakeshore West" to Color(0xFF8D0134),
    "Milton" to Color(0xFFDE6428),
    "Richmond Hill" to Color(0xFF01ABE8),
    "Stouffville" to Color(0xFF805116)
)