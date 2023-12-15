package dev.jsinco.hoarder.objects

import dev.jsinco.hoarder.HoarderEvent
import java.util.*

class Time {

    val remainingTime: Long = HoarderEvent.endTime - System.currentTimeMillis()
    private val calendar = Calendar.getInstance()
    init {
        calendar.setTimeInMillis(remainingTime)
    }

    val hours = remainingTime / 3600000
    val mins = if (remainingTime > 0) calendar[Calendar.MINUTE] else 0
    val secs = if (remainingTime > 0) calendar[Calendar.SECOND] else 0
}