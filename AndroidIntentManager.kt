package com.yesmin.ai.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.yesmin.ai.data.ActionData

object AndroidIntentManager {

    fun executeAction(context: Context, action: ActionData) {
        try {
            when (action.action) {
                "YOUTUBE_PLAY", "YOUTUBE_SEARCH" -> {
                    val query = Uri.encode(action.payload)
                    val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://results?search_query=$query"))
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$query"))
                    try {
                        context.startActivity(appIntent)
                    } catch (e: Exception) {
                        context.startActivity(webIntent)
                    }
                }

                "PHONE_CALL" -> {
                    // Safe dialer intent: pre-fills number in dialer without making blind calls
                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${action.payload}")
                    }
                    context.startActivity(dialIntent)
                }

                "SEND_SMS" -> {
                    val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("smsto:${action.target}")
                        putExtra("sms_body", action.payload)
                    }
                    context.startActivity(smsIntent)
                }

                "WHATSAPP_MSG" -> {
                    val url = "https://api.whatsapp.com/send?text=${Uri.encode(action.payload)}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }

                "GOOGLE_MAPS" -> {
                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(action.payload)}"))
                    mapIntent.setPackage("com.google.android.apps.maps")
                    try {
                        context.startActivity(mapIntent)
                    } catch (e: Exception) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${Uri.encode(action.payload)}")))
                    }
                }

                "OPEN_APP" -> {
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(action.target)
                    if (launchIntent != null) {
                        context.startActivity(launchIntent)
                    } else {
                        Toast.makeText(context, "App not found on device: ${action.target}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Intent execution failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
