package com.fqe.android.data.network

import com.fqe.android.data.model.AssignStudentRequest
import com.fqe.android.data.model.TutorProfileResponse
import com.fqe.android.data.model.TutorStudentDetailResponse
import com.fqe.android.data.model.TutorStudentResponse
import com.fqe.android.data.model.StudentSessionResponse
import com.fqe.android.data.model.UpdateTutorProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TutorApi {
    @GET("data/tutors/{id}")
    suspend fun getTutor(
        @Path("id") tutorId: Int,
        @Header("Authorization") authorization: String
    ): Response<TutorProfileResponse>

    @GET("data/tutors/{id}/students")
    suspend fun getTutorStudents(
        @Path("id") tutorId: Int,
        @Header("Authorization") authorization: String
    ): Response<List<TutorStudentResponse>>

    @GET("data/students/{id}")
    suspend fun getStudent(
        @Path("id") studentId: Int,
        @Header("Authorization") authorization: String
    ): Response<TutorStudentDetailResponse>

    @GET("data/students/{id}/sessions")
    suspend fun getStudentSessions(
        @Path("id") studentId: Int,
        @Header("Authorization") authorization: String
    ): Response<List<StudentSessionResponse>>

    @POST("data/tutors/assign-student")
    suspend fun assignStudent(
        @Header("Authorization") authorization: String,
        @Body request: AssignStudentRequest
    ): Response<Unit>

    @PUT("data/tutors/{id}")
    suspend fun updateTutor(
        @Path("id") tutorId: Int,
        @Header("Authorization") authorization: String,
        @Body request: UpdateTutorProfileRequest
    ): Response<TutorProfileResponse>
}