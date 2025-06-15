package com.example.playlistmaker.utils

import com.example.playlistmaker.core.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T> debounce(
    delayMillis: Long,
    coroutineScope: CoroutineScope,
    useLastParam: Boolean,
    action: (T) -> Unit
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        if (useLastParam) {
            debounceJob?.cancel()
        }
        if (debounceJob?.isCompleted != false || useLastParam) {
            debounceJob = coroutineScope.launch {
                when (param) {
                    is CharSequence -> {
                        delay(delayMillis)
                        action(param)
                    }

                    is Track -> {
                        action(param)
                        delay(delayMillis)
                    }
                }
            }
        }
    }
}