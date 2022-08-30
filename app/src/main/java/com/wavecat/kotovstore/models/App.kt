package com.wavecat.kotovstore.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class App(
    val id: Int = 0,
    val name: String = "",
    val author: String = "",
    val category: String = "",
    val packageName: String = "",
    val versionCode: Long = 0,
    val icon: String = ""
)
