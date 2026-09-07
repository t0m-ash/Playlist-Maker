package com.practicum.playlistmaker.domain.sharing.models

data class EmailData(
    val address: String,
    val subject: String,
    val text: String,
)
