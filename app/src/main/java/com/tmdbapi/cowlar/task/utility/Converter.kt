package com.tmdbapi.cowlar.task.utility

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.chrono.ChronoLocalDate
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DecimalStyle
import java.time.temporal.TemporalAccessor
import java.util.*
import java.util.concurrent.TimeUnit


object Converter {

    val times = listOf(
        TimeUnit.DAYS.toMillis(365),
        TimeUnit.DAYS.toMillis(30),
        TimeUnit.DAYS.toMillis(1),
        TimeUnit.HOURS.toMillis(1),
        TimeUnit.MINUTES.toMillis(1),
        TimeUnit.SECONDS.toMillis(1)
    )
    private val timesString = listOf("year", "month", "day", "hr", "min", "sec")

    @Throws(ParseException::class)
    fun toDuration(date: String, now: Date = Date()): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
        format.timeZone = TimeZone.getTimeZone("UTC")

        val past = format.parse(date)
        val duration = TimeUnit.MILLISECONDS.toMillis(now.time - past!!.time)

        val response = StringBuilder()
        for (i in times.indices) {
            val current = times[i]
            val temp = duration / current
            if (temp >= 1) {
                response.append(temp.toInt()).append(" ").append(timesString[i])
                    .append(if (temp.toInt() != 1) "s" else "").append(" ago")
                break
            }
        }
        return if ("" == response.toString()) "0 secs ago" else response.toString()
    }


    fun getSize(size: Long): String {
        val s: String
        val kb = size.toString().toDouble() / 1024
        val mb = kb / 1024
        val gb = mb / 1024
        val tb = gb / 1024
        s = when {
            size < 1024L -> "$size B"
            size < 1024L * 1024 -> String.format("%.2f", kb) + " KB"
            size < 1024L * 1024 * 1024 -> String.format("%.2f", mb) + " MB"
            size < 1024L * 1024 * 1024 * 1024 -> String.format("%.2f", gb) + " GB"
            else -> String.format("%.2f", tb) + " TB"
        }
        return s
    }

    fun convertMinutes(min: Int): String {
        if (min <= 60) return "$min min"
        return "${min / 60} hr ${min % 60} min"
    }

    fun parseDate(
        date: String,
        srcPattern: String = "yyyy-MM-dd",
        dstPattern: String = "EEEE dd, yyyy"
    ): String {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val srcFormat = DateTimeFormatter.ofPattern(srcPattern)
            val dstFormat = DateTimeFormatter.ofPattern(dstPattern, Locale.ENGLISH)
            val datetime = LocalDate.parse(date, srcFormat)
            return datetime.format(dstFormat)
        }else{
            return try {
                // Create a SimpleDateFormat object for the source pattern
                val srcFormat = SimpleDateFormat(srcPattern, Locale.ENGLISH)
                // Create a SimpleDateFormat object for the destination pattern
                val dstFormat = SimpleDateFormat(dstPattern, Locale.ENGLISH)

                // Parse the input date string into a Date object
                val parsedDate = srcFormat.parse(date)

                // Format the Date object into the desired string format
                parsedDate?.let { dstFormat.format(it) } ?: ""
            } catch (e: Exception) {
                // Handle parsing error, possibly by returning an empty string or a default value
                ""
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun yearsBetween(startDate: String, endDate: String): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val start = LocalDate.parse(startDate, formatter)
        val end = LocalDate.parse(endDate, formatter)
        return Period.between(start, end).years
    }

    fun getDate(): String {
        val time = System.currentTimeMillis()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return formatter.format(time)!!
    }

    fun dateToLocalDate(date: String): LocalDate {
        val pattern = "MMM dd, yyyy"

        val chronology = IsoChronology.INSTANCE
        val df = DateTimeFormatterBuilder()
            .parseLenient().appendPattern(pattern).toFormatter()
            .withChronology(chronology)
            .withDecimalStyle(DecimalStyle.of(Locale.ENGLISH))
        val temporal: TemporalAccessor = df.parse(date)
        val cDate: ChronoLocalDate = chronology.date(temporal)
        return LocalDate.from(cDate)
    }

    fun toHumanSize(size: Long): String {
        val kb = size.toString().toDouble() / 1024
        val mb = kb / 1024
        val gb = mb / 1024
        val tb = gb / 1024
        return when {
            size < 1024L -> "$size Bytes"
            size < 1024L * 1024 -> String.format("%.2f", kb) + " KB"
            size < 1024L * 1024 * 1024 -> String.format("%.2f", mb) + " MB"
            size < 1024L * 1024 * 1024 * 1024 -> String.format("%.2f", gb) + " GB"
            else -> String.format("%.2f", tb) + " TB"
        }
    }

}