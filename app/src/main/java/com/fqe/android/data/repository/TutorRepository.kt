package com.fqe.android.data.repository

import com.fqe.android.data.model.AssignStudentRequest
import com.fqe.android.data.model.GatewayErrorResponse
import com.fqe.android.data.model.StudentSessionResponse
import com.fqe.android.data.model.TutorProfileResponse
import com.fqe.android.data.model.TutorStudentDetailResponse
import com.fqe.android.data.model.TutorStudentResponse
import com.fqe.android.data.model.UpdateTutorProfileRequest
import com.fqe.android.data.network.TutorApi
import com.fqe.android.data.session.SessionStore
import com.fqe.android.util.JwtUtils
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

class TutorRepository(
    private val tutorApi: TutorApi,
    private val sessionStore: SessionStore,
    private val gson: Gson = Gson()
) {
    suspend fun getTutorProfile(): TutorResult<TutorProfileResponse> {
        val session = resolveSession() ?: return TutorResult.Error("Sesion invalida. Inicia sesion de nuevo.")

        return safeCall {
            tutorApi.getTutor(session.tutorId, session.authorization)
        }
    }

    suspend fun getAssignedStudents(): TutorResult<List<TutorStudentResponse>> {
        val session = resolveSession() ?: return TutorResult.Error("Sesion invalida. Inicia sesion de nuevo.")

        return safeStudentCall {
            tutorApi.getTutorStudents(session.tutorId, session.authorization)
        }
    }

    suspend fun getStudentDetail(studentId: Int): TutorResult<TutorStudentDetailResponse> {
        val session = resolveSession() ?: return TutorResult.Error("Sesion invalida. Inicia sesion de nuevo.")

        return safeStudentDetailCall {
            tutorApi.getStudent(studentId, session.authorization)
        }
    }

    suspend fun getStudentSessions(studentId: Int): TutorResult<List<StudentSessionResponse>> {
        val session = resolveSession() ?: return TutorResult.Error("Sesion invalida. Inicia sesion de nuevo.")

        return safeSessionHistoryCall {
            tutorApi.getStudentSessions(studentId, session.authorization)
        }
    }

    suspend fun assignStudentByEmail(studentEmail: String): TutorResult<Unit> {
        val session = resolveSession() ?: return TutorResult.Error("Sesion invalida. Inicia sesion de nuevo.")

        return safeUnitCall {
            tutorApi.assignStudent(
                authorization = session.authorization,
                request = AssignStudentRequest(
                    studentEmail = studentEmail,
                    tutorEmail = session.tutorEmail
                )
            )
        }
    }

    suspend fun updateTutorProfile(request: UpdateTutorProfileRequest): TutorResult<TutorProfileResponse> {
        val session = resolveSession() ?: return TutorResult.Error("Sesion invalida. Inicia sesion de nuevo.")

        return safeCall {
            tutorApi.updateTutor(
                tutorId = session.tutorId,
                authorization = session.authorization,
                request = request
            )
        }
    }

    private suspend fun resolveSession(): TutorSession? {
        val token = sessionStore.tokenFlow.first().orEmpty()
        val tutorId = JwtUtils.extractUserId(token)
        val tutorEmail = JwtUtils.extractEmail(token)

        if (token.isBlank() || tutorId == null || tutorEmail.isNullOrBlank()) {
            return null
        }

        return TutorSession(
            tutorId = tutorId,
            tutorEmail = tutorEmail,
            authorization = "Bearer $token"
        )
    }

    private suspend fun safeCall(block: suspend () -> retrofit2.Response<TutorProfileResponse>): TutorResult<TutorProfileResponse> {
        return try {
            val response = block()
            if (response.isSuccessful) {
                val body = response.body()
                if (body == null) {
                    TutorResult.Error("Error procesando la respuesta del servidor")
                } else {
                    TutorResult.Success(body)
                }
            } else {
                val errorBody = response.errorBody()?.string().orEmpty()
                val parsed = runCatching { gson.fromJson(errorBody, GatewayErrorResponse::class.java) }.getOrNull()
                val message = parsed?.message
                    ?: parsed?.error
                    ?: "No se pudo completar la operacion (${response.code()})"
                TutorResult.Error(message)
            }
        } catch (_: Exception) {
            TutorResult.Error("No se pudo conectar con el servidor. Verifica red o gateway.")
        }
    }

    private suspend fun safeStudentCall(block: suspend () -> retrofit2.Response<List<TutorStudentResponse>>): TutorResult<List<TutorStudentResponse>> {
        return try {
            val response = block()
            if (response.isSuccessful) {
                TutorResult.Success(response.body().orEmpty())
            } else {
                val errorBody = response.errorBody()?.string().orEmpty()
                val parsed = runCatching { gson.fromJson(errorBody, GatewayErrorResponse::class.java) }.getOrNull()
                val message = parsed?.message
                    ?: parsed?.error
                    ?: "No se pudo completar la operacion (${response.code()})"
                TutorResult.Error(message)
            }
        } catch (_: Exception) {
            TutorResult.Error("No se pudo conectar con el servidor. Verifica red o gateway.")
        }
    }

    private suspend fun safeStudentDetailCall(block: suspend () -> retrofit2.Response<TutorStudentDetailResponse>): TutorResult<TutorStudentDetailResponse> {
        return try {
            val response = block()
            if (response.isSuccessful) {
                val body = response.body()
                if (body == null) {
                    TutorResult.Error("Error procesando la respuesta del servidor")
                } else {
                    TutorResult.Success(body)
                }
            } else {
                val errorBody = response.errorBody()?.string().orEmpty()
                val parsed = runCatching { gson.fromJson(errorBody, GatewayErrorResponse::class.java) }.getOrNull()
                val message = parsed?.message
                    ?: parsed?.error
                    ?: "No se pudo completar la operacion (${response.code()})"
                TutorResult.Error(message)
            }
        } catch (_: Exception) {
            TutorResult.Error("No se pudo conectar con el servidor. Verifica red o gateway.")
        }
    }

    private suspend fun safeSessionHistoryCall(block: suspend () -> retrofit2.Response<List<StudentSessionResponse>>): TutorResult<List<StudentSessionResponse>> {
        return try {
            val response = block()
            if (response.isSuccessful) {
                TutorResult.Success(response.body().orEmpty())
            } else {
                val errorBody = response.errorBody()?.string().orEmpty()
                val parsed = runCatching { gson.fromJson(errorBody, GatewayErrorResponse::class.java) }.getOrNull()
                val message = parsed?.message
                    ?: parsed?.error
                    ?: "No se pudo completar la operacion (${response.code()})"
                TutorResult.Error(message)
            }
        } catch (_: Exception) {
            TutorResult.Error("No se pudo conectar con el servidor. Verifica red o gateway.")
        }
    }

    private suspend fun safeUnitCall(block: suspend () -> retrofit2.Response<Unit>): TutorResult<Unit> {
        return try {
            val response = block()
            if (response.isSuccessful) {
                TutorResult.Success(Unit)
            } else {
                val errorBody = response.errorBody()?.string().orEmpty()
                val parsed = runCatching { gson.fromJson(errorBody, GatewayErrorResponse::class.java) }.getOrNull()
                val message = parsed?.message
                    ?: parsed?.error
                    ?: "No se pudo completar la operacion (${response.code()})"
                TutorResult.Error(message)
            }
        } catch (_: Exception) {
            TutorResult.Error("No se pudo conectar con el servidor. Verifica red o gateway.")
        }
    }
}

sealed class TutorResult<out T> {
    data class Success<T>(val data: T) : TutorResult<T>()
    data class Error(val message: String) : TutorResult<Nothing>()
}

private data class TutorSession(
    val tutorId: Int,
    val tutorEmail: String,
    val authorization: String
)