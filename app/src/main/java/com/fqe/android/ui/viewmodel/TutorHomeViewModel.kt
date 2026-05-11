package com.fqe.android.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fqe.android.data.model.TutorStudentResponse
import com.fqe.android.data.repository.TutorRepository
import com.fqe.android.data.repository.TutorResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TutorHomeUiState(
    val loading: Boolean = true,
    val isRefreshing: Boolean = false,
    val students: List<TutorStudentResponse> = emptyList(),
    val error: String? = null,
    val isAssignDialogOpen: Boolean = false,
    val assignStudentEmail: String = "",
    val assignInProgress: Boolean = false,
    val assignError: String? = null,
    val feedbackMessage: String? = null
)

class TutorHomeViewModel(
    private val tutorRepository: TutorRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TutorHomeUiState())
    val uiState: StateFlow<TutorHomeUiState> = _uiState.asStateFlow()

    init {
        loadStudents()
    }

    fun loadStudents() {
        fetchStudents(
            showLoader = _uiState.value.students.isEmpty(),
            showRefreshing = false
        )
    }

    fun refreshStudents() {
        fetchStudents(
            showLoader = _uiState.value.students.isEmpty(),
            showRefreshing = _uiState.value.students.isNotEmpty()
        )
    }

    fun openAssignDialog() {
        _uiState.update {
            it.copy(
                isAssignDialogOpen = true,
                assignStudentEmail = "",
                assignError = null,
                feedbackMessage = null
            )
        }
    }

    fun dismissAssignDialog() {
        _uiState.update {
            it.copy(
                isAssignDialogOpen = false,
                assignStudentEmail = "",
                assignError = null,
                assignInProgress = false
            )
        }
    }

    fun onAssignStudentEmailChange(value: String) {
        _uiState.update {
            it.copy(
                assignStudentEmail = value,
                assignError = null
            )
        }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    fun assignStudent() {
        val email = _uiState.value.assignStudentEmail.trim()
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(assignError = "Ingresa un email valido.") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    assignInProgress = true,
                    assignError = null,
                    feedbackMessage = null
                )
            }

            when (val result = tutorRepository.assignStudentByEmail(email)) {
                is TutorResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isAssignDialogOpen = false,
                            assignStudentEmail = "",
                            assignInProgress = false,
                            assignError = null,
                            feedbackMessage = "Alumno asignado correctamente."
                        )
                    }
                    fetchStudents(showLoader = false, showRefreshing = false)
                }
                is TutorResult.Error -> {
                    _uiState.update {
                        it.copy(
                            assignInProgress = false,
                            assignError = result.message
                        )
                    }
                }
            }
        }
    }

    private fun fetchStudents(showLoader: Boolean, showRefreshing: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loading = showLoader,
                    isRefreshing = showRefreshing,
                    error = null
                )
            }

            when (val result = tutorRepository.getAssignedStudents()) {
                is TutorResult.Success -> {
                    _uiState.update {
                        it.copy(
                            loading = false,
                            isRefreshing = false,
                            students = result.data.sortedBy { student -> student.name.orEmpty() },
                            error = null
                        )
                    }
                }
                is TutorResult.Error -> {
                    _uiState.update {
                        it.copy(
                            loading = false,
                            isRefreshing = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}

class TutorHomeViewModelFactory(
    private val tutorRepository: TutorRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TutorHomeViewModel(tutorRepository) as T
    }
}