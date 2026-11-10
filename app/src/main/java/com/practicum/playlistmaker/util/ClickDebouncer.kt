package com.practicum.playlistmaker.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClickDebouncer(
    private val scope: CoroutineScope,
) {

    private var isClickAllowed = true

    fun tryClick(): Boolean {
        if (!isClickAllowed) return false

        isClickAllowed = false
        scope.launch {
            delay(CLICK_DEBOUNCE_DELAY)
            isClickAllowed = true
        }
        return true
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
