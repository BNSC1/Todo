package com.bn.todo.util

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

object TimeUtil {
    val calendar: Calendar get() = Calendar.getInstance()
    private var zoneOffset = ZoneId.systemDefault().rules.getOffset(Instant.now())

    fun getOffsetDateTime(
        instant: Instant,
        zoneOffset: ZoneOffset = this.zoneOffset
    ): OffsetDateTime =
        instant.atOffset(zoneOffset)

}