package com.yesmin.ai.data

import com.google.firebase.appcheck.FirebaseAppCheck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class ActionData(
    val action: String,
    val requiresConfirmation: Boolean,
    val title: String,
    val target: String,
    val payload: String,
    val description: String
)

data class YesminResponse(
    val reply: String,
    val action: ActionData?
)

class GeminiRepository {

    // Secure architecture: Queries backend proxy with Firebase App Check token.
    // Zero hardcoded API keys or secrets in the client APK.
    suspend fun sendChatMessage(
        message: String,
        history: List<Pair<String, String>>,
        isHindiPreferred: Boolean
    ): Result<YesminResponse> = withContext(Dispatchers.IO) {
        try {
            // Retrieve Firebase App Check token for hardware attestation
            val appCheckToken = try {
                FirebaseAppCheck.getInstance().getAppCheckToken(false)
            } catch (e: Exception) {
                null
            }

            val serverUrl = URL("https://your-firebase-ai-backend.run.app/api/chat")
            val conn = (serverUrl.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("User-Agent", "YESMIN-Android-Client")
                // Inject App Check token to guarantee authentic Android installation
                appCheckToken?.let { setRequestProperty("X-Firebase-AppCheck", it.token) }
            }

            val requestJson = JSONObject().apply {
                put("message", message)
                put("language", if (isHindiPreferred) "hi" else "en")
            }

            conn.outputStream.use { os ->
                os.write(requestJson.toString().toByteArray())
            }

            if (conn.responseCode == 200) {
                val responseString = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonObj = JSONObject(responseString)
                val reply = jsonObj.optString("reply", "Understood.")

                var actionData: ActionData? = null
                if (jsonObj.has("action") && !jsonObj.isNull("action")) {
                    val actObj = jsonObj.getJSONObject("action")
                    actionData = ActionData(
                        action = actObj.optString("action"),
                        requiresConfirmation = actObj.optBoolean("requiresConfirmation", true),
                        title = actObj.optString("title"),
                        target = actObj.optString("target"),
                        payload = actObj.optString("payload"),
                        description = actObj.optString("description")
                    )
                }

                Result.success(YesminResponse(reply, actionData))
            } else {
                Result.failure(Exception("Backend responded with HTTP ${conn.responseCode}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
