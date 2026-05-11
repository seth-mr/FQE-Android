package com.fqe.android.data.network

import com.fqe.android.data.model.AuthTokenResponse
import com.fqe.android.data.model.LoginRequest
import com.fqe.android.data.model.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthTokenResponse>

    @POST("auth/register")
    suspend fun signup(@Body request: SignupRequest): Response<AuthTokenResponse>
}
