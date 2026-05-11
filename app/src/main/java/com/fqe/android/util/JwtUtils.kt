package com.fqe.android.util

import android.util.Base64
import org.json.JSONObject

object JwtUtils {
    fun extractRole(token: String?): String? {
           return decodePayload(token)?.let { payload ->
              if (payload.has("role")) payload.optString("role") else null
           }
    }

    fun extractEmail(token: String?): String? {
        return decodePayload(token)?.let { payload ->
            if (payload.has("email")) payload.optString("email") else null
        }?.takeIf { it.isNotBlank() }
    }

    fun extractUserId(token: String?): Int? {
        val rawValue = decodePayload(token)?.optString("sub")
            ?: decodePayload(token)?.optString("userId")
            ?: return null

        return rawValue.toIntOrNull()
    }

    private fun decodePayload(token: String?): JSONObject? {
        if (token.isNullOrBlank()) return null

        return runCatching {
            val parts = token.split(".")
            if (parts.size < 2) return null
            val payload = parts[1]
            val normalized = payload.padEnd(payload.length + (4 - payload.length % 4) % 4, '=')
            val decoded = Base64.decode(normalized, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            JSONObject(String(decoded))
        }.getOrNull()
    }
}
