package com.bn.todo.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {
    val currentTime: Date get() = Calendar.getInstance().time

    @SuppressLint("SimpleDateFormat")
    fun getLocaleMonth(month: Int): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, month - 1)
        val dateFormat = SimpleDateFormat("MMMM")
        return dateFormat.format(cal.time)
    }

    fun convertDateStringToDate(dateString: String?, format: String = "yyyy-MM-dd'T'HH:mm:ss") =
        dateString?.let {
            SimpleDateFormat(format, Locale.getDefault()).parse(it)
        }

    fun convertDateToDateString(date: Date?, format: String = "yyyy-MM-dd") =
        date?.let {
            SimpleDateFormat(format, Locale.getDefault()).format(date)
        }

}