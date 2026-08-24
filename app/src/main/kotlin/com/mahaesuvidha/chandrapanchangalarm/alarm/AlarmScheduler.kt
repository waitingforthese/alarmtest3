package com.mahaesuvidha.chandrapanchangalarm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

import com.mahaesuvidha.chandrapanchangalarm.model.LiveMoonCalculator
import com.mahaesuvidha.chandrapanchangalarm.model.LiveSunCalculator
import com.mahaesuvidha.chandrapanchangalarm.model.LivePanchangCalculator
import com.mahaesuvidha.chandrapanchangalarm.settings.AlarmPrefs

class AlarmScheduler(
    private val context: Context
) {

    private val alarmManager =
        context.getSystemService(
            AlarmManager::class.java
        )

    // ==========================================
    // TEST ALARMS
    // ==========================================

    fun scheduleRashiAlarm(
        delayMillis: Long
    ) {
        schedule(
            id = 101,
            at = System.currentTimeMillis() + delayMillis,
            title = "🌙 चंद्र राशी बदल",
            message = "ही राशी बदलाची टेस्ट सूचना आहे."
        )
    }

    fun scheduleNakshatraAlarm(
        delayMillis: Long
    ) {
        schedule(
            id = 102,
            at = System.currentTimeMillis() + delayMillis,
            title = "🌙 चंद्र नक्षत्र बदल",
            message = "ही नक्षत्र बदलाची टेस्ट सूचना आहे."
        )
    }

    fun scheduleCharanAlarm(
        delayMillis: Long
    ) {
        schedule(
            id = 103,
            at = System.currentTimeMillis() + delayMillis,
            title = "🌙 चंद्र चरण बदल",
            message = "ही चरण बदलाची टेस्ट सूचना आहे."
        )
    }

    // ==========================================
    // SCHEDULE ALL ALARMS
    // ==========================================

    fun scheduleAll() {

        cancelAll()

        val prefs = AlarmPrefs(context)

        // ==========================================
        // MOON ALARMS
        // ==========================================

        if (prefs.moon) {

            val moon =
                LiveMoonCalculator
                    .getCurrentMoonState()

            if (prefs.rashi) {

                schedule(
                    id = 1,
                    at = moon.nextRashiMillis,
                    title = "🌙 चंद्र राशी बदल",
                    message = moon.nextRashi
                )
            }

            if (prefs.nak) {

                schedule(
                    id = 2,
                    at = moon.nextNakshatraMillis,
                    title = "🌙 चंद्र नक्षत्र बदल",
                    message = moon.nextNakshatra
                )
            }

            if (prefs.pada) {

                schedule(
                    id = 3,
                    at = moon.nextCharanMillis,
                    title = "🌙 चंद्र चरण बदल",
                    message = moon.nextCharan
                )
            }
        }

        // ==========================================
        // SUN ALARMS
        // ==========================================

        if (prefs.sun) {

            val sun =
                LiveSunCalculator
                    .getCurrentSunState()

            if (prefs.rashi) {

                schedule(
                    id = 11,
                    at = sun.nextRashiMillis,
                    title = "☀️ सूर्य राशी बदल",
                    message = sun.nextRashi
                )
            }

            if (prefs.nak) {

                schedule(
                    id = 12,
                    at = sun.nextNakshatraMillis,
                    title = "☀️ सूर्य नक्षत्र बदल",
                    message = sun.nextNakshatra
                )
            }

            if (prefs.pada) {

                schedule(
                    id = 13,
                    at = sun.nextCharanMillis,
                    title = "☀️ सूर्य चरण बदल",
                    message = sun.nextCharan
                )
            }
        }

        // ==========================================
        // PANCHANG ALARMS
        // ==========================================

        if (prefs.panchang) {

            val panchang =
                LivePanchangCalculator
                    .getCurrentPanchangState()

            if (prefs.tithi) {

                schedule(
                    id = 21,
                    at = panchang.nextTithiMillis,
                    title = "🔔 तिथी बदल",
                    message =
                        "${panchang.tithi} → ${panchang.nextTithi}"
                )
            }

            if (prefs.yoga) {

                schedule(
                    id = 22,
                    at = panchang.nextYogaMillis,
                    title = "🔔 योग बदल",
                    message =
                        "${panchang.yoga} → ${panchang.nextYoga}"
                )
            }

            if (prefs.karana) {

                schedule(
                    id = 23,
                    at = panchang.nextKaranaMillis,
                    title = "🔔 करण बदल",
                    message =
                        "${panchang.karana} → ${panchang.nextKarana}"
                )
            }

            if (prefs.paksha) {

                schedule(
                    id = 24,
                    at = panchang.nextPakshaMillis,
                    title = "🔔 पक्ष बदल",
                    message =
                        "${panchang.paksha} → ${panchang.nextPaksha}"
                )
            }
            // ==========================================
// PRAHAR ALARM
// ==========================================

if (prefs.prahar) {

    schedule(
        id = 26,
        at = panchang.nextPraharMillis,
        title = "⌛ प्रहर बदल",
        message =
            "${panchang.prahar} → ${panchang.nextPrahar}"
    )
}


// ==========================================
// LAGNA ALARM
// ==========================================

if (prefs.lagna) {

    schedule(
        id = 27,
        at = panchang.nextLagnaMillis,
        title = "⭐ लग्न बदल",
        message =
            "${panchang.lagna} → ${panchang.nextLagna}"
    )
}
        }
    }

    // ==========================================
    // GENERAL TEST ALARM
    // ==========================================

    fun scheduleTest(
        type: String
    ) {

        schedule(
            id = 99,
            at = System.currentTimeMillis() + 10_000L,
            title = "🔔 चंद्र सूर्य अलार्म",
            message = "$type Test Alarm आहे."
        )
    }

    // ==========================================
    // MAIN SCHEDULE FUNCTION
    // ==========================================

    private fun schedule(
        id: Int,
        at: Long,
        title: String,
        message: String
    ) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S
        ) {

            if (
                !alarmManager
                    .canScheduleExactAlarms()
            ) {

                try {

                    val intent =
                        Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                        )

                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    )

                    context.startActivity(intent)

                } catch (_: Exception) {
                }

                return
            }
        }

        val intent =
            Intent(
                context,
                AlarmReceiver::class.java
            )

        intent.putExtra(
            "title",
            title
        )

        intent.putExtra(
            "message",
            message
        )

        intent.putExtra(
            "id",
            id
        )

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            at,
            pendingIntent
        )
    }

    // ==========================================
    // CANCEL ALL ALARMS
    // ==========================================

    fun cancelAll() {

        for (id in 1..3) {
            cancel(id)
        }

        for (id in 11..13) {
            cancel(id)
        }

     for (id in 21..24) {
            cancel(id)
        }

        cancel(26)
        cancel(27)

        cancel(99)
        cancel(101)
        cancel(102)
        cancel(103)
    }

    // ==========================================
    // CANCEL SINGLE ALARM
    // ==========================================

    private fun cancel(
        id: Int
    ) {

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                id,
                Intent(
                    context,
                    AlarmReceiver::class.java
                ),
                PendingIntent.FLAG_NO_CREATE or
                        PendingIntent.FLAG_IMMUTABLE
            )

        if (pendingIntent != null) {

            alarmManager.cancel(
                pendingIntent
            )

            pendingIntent.cancel()
        }
    }
}
