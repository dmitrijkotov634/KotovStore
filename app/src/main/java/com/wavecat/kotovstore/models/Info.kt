package com.wavecat.kotovstore.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Info(
    val notice: String? = null,
    val description: String = "",
    val screenshots: List<String> = emptyList(),
    val apk: String = ""
)
