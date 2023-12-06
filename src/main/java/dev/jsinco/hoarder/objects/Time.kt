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
    val mins = calendar[Calendar.MINUTE]
    val secs = calendar[Calendar.SECOND]
}