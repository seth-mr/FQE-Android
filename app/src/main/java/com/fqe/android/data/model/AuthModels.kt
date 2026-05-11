package com.fqe.android.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String,
    val age: Int,
    val gender: String,
    val country: String,
    val role: String,
    val neurodivergency: String? = null,
    val degree: String? = null
)

data class AuthTokenResponse(
    val token: String
)

data class GatewayErrorResponse(
    val message: String? = null,
    val errors: Map<String, List<String>>? = null,
    val error: String? = null
)
