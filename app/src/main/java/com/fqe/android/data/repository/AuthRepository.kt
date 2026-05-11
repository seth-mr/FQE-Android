package com.fqe.android.data.repository

import com.fqe.android.data.model.AuthTokenResponse
import com.fqe.android.data.model.GatewayErrorResponse
import com.fqe.android.data.model.LoginRequest
import com.fqe.android.data.model.SignupRequest
import com.fqe.android.data.network.AuthApi
import com.google.gson.Gson

class AuthRepository(
    private val authApi: AuthApi,
    private val gson: Gson = Gson()
) {
    suspend fun login(email: String, password: String): AuthResult<AuthTokenResponse> {
        return safeCall {
            authApi.login(LoginRequest(email = email, password = password))
        }
    }

    suspend fun signup(request: SignupRequest): AuthResult<AuthTokenResponse> {
        return safeCall {
            authApi.signup(request)
        }
    }

    private suspend fun safeCall(block: suspend () -> retrofit2.Response<AuthTokenResponse>): AuthResult<AuthTokenResponse> {
        return try {
            val response = block()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.token.isNullOrBlank()) {
                    AuthResult.Error("Error procesando la respuesta del servidor")
                } else {
                    AuthResult.Success(body!!)
                }
            } else {
                val errorBody = response.errorBody()?.string().orEmpty()
                val parsed = runCatching { gson.fromJson(errorBody, GatewayErrorResponse::class.java) }.getOrNull()
                val message = parsed?.message
                    ?: parsed?.error
                    ?: "Error de autenticacion (${response.code()})"
                AuthResult.Error(message)
            }
        } catch (e: Exception) {
            AuthResult.Error("No se pudo conectar con el servidor. Verifica red o gateway.")
        }
    }
}

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}
