package com.example.mysihat.data

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class NotificationType {
    SUCCESS,
    ERROR,
    INFO
}

data class AppNotification(
    val id: Long = System.nanoTime(),
    val message: String,
    val type: NotificationType = NotificationType.SUCCESS,
    val durationMillis: Long = 5000L // Auto-hide after 5 seconds (5000ms)
)

object NotificationService {
    val currentNotification = mutableStateOf<AppNotification?>(null)
    private var dismissJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    fun showSuccess(message: String) {
        show(message, NotificationType.SUCCESS)
    }

    fun showError(message: String) {
        show(message, NotificationType.ERROR)
    }

    fun showInfo(message: String) {
        show(message, NotificationType.INFO)
    }

    private fun show(message: String, type: NotificationType) {
        dismissJob?.cancel()
        val notif = AppNotification(
            id = System.nanoTime(),
            message = message,
            type = type,
            durationMillis = 5000L
        )
        currentNotification.value = notif
        dismissJob = scope.launch {
            delay(notif.durationMillis)
            if (currentNotification.value?.id == notif.id) {
                currentNotification.value = null
            }
        }
    }

    fun dismiss() {
        dismissJob?.cancel()
        currentNotification.value = null
    }
}
