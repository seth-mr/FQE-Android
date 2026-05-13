package com.fqe.android.data.model

data class TutorProfileResponse(
    val idTutor: Int,
    val name: String,
    val email: String,
    val country: String,
    val gender: String? = null,
    val age: Int? = null,
    val degree: String? = null,
    val registrationDate: String? = null,
    val students: List<TutorStudentSummary> = emptyList()
)

data class TutorStudentSummary(
    val idStudent: Int
)

data class TutorStudentResponse(
    val idStudent: Int,
    val idTutor: Int? = null,
    val name: String? = null,
    val email: String? = null,
    val age: Int,
    val gender: String? = null,
    val country: String? = null,
    val neurodivergency: String? = null,
    val registrationDate: String? = null
)

data class TutorStudentDetailResponse(
    val idStudent: Int,
    val idTutor: Int? = null,
    val name: String? = null,
    val email: String? = null,
    val age: Int,
    val gender: String? = null,
    val country: String? = null,
    val neurodivergency: String? = null,
    val registrationDate: String? = null,
    val tutor: StudentTutorResponse? = null
)

data class StudentTutorResponse(
    val idTutor: Int? = null,
    val name: String? = null,
    val email: String? = null,
    val country: String? = null
)

data class StudentSessionResponse(
    val idSession: Int,
    val idStudent: Int,
    val beginningDate: String? = null,
    val endDate: String? = null,
    val device: String? = null,
    val student: SessionStudentResponse? = null,
    val levelResults: List<SessionLevelResultResponse> = emptyList()
)

data class SessionStudentResponse(
    val idStudent: Int,
    val idTutor: Int? = null,
    val name: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    val country: String? = null
)

data class SessionLevelResultResponse(
    val idResult: Int,
    val idLevel: Int,
    val idSession: Int,
    val completed: Boolean? = null
)

data class AssignStudentRequest(
    val studentEmail: String,
    val tutorEmail: String
)

data class UpdateTutorProfileRequest(
    val name: String,
    val email: String,
    val country: String,
    val gender: String?,
    val age: Int?,
    val degree: String?
)