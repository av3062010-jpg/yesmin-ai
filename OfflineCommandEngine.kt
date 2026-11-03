package com.yesmin.ai.utils

import com.yesmin.ai.data.ActionData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object OfflineCommandEngine {

    fun matchCommand(rawQuery: String): Pair<String, ActionData?>? {
        val query = rawQuery.lowercase(Locale.ROOT).trim()

        if (query.contains("time") || query.contains("समय") || query.contains("टाइम")) {
            val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            val reply = if (query.contains("समय") || query.contains("टाइम")) "अभी समय $time हुआ है।" else "The current time is $time."
            return Pair(reply, null)
        }

        if (query.contains("date") || query.contains("तारीख") || query.contains("दिनांक")) {
            val date = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(Date())
            val reply = if (query.contains("तारीख")) "आज $date है।" else "Today is $date."
            return Pair(reply, null)
        }

        if (query.contains("youtube") || query.contains("play music") || query.contains("गाना")) {
            val song = query.replace("play", "").replace("youtube", "").replace("गाना", "").trim()
            val target = if (song.isNotEmpty()) song else "lofi beats"
            val action = ActionData(
                action = "YOUTUBE_PLAY",
                requiresConfirmation = true,
                title = "Play on YouTube",
                target = target,
                payload = target,
                description = "Stream $target on YouTube."
            )
            return Pair("Launching YouTube music player for: $target", action)
        }

        if (query.startsWith("call ") || query.contains("कॉल करो")) {
            val contact = query.replace("call", "").replace("कॉल करो", "").trim()
            val action = ActionData(
                action = "PHONE_CALL",
                requiresConfirmation = true,
                title = "Initiate Call",
                target = contact,
                payload = contact,
                description = "Security check: Dial $contact. Manual confirmation required."
            )
            return Pair("Prepared call to $contact. Confirmation needed.", action)
        }

        return null
    }
}
