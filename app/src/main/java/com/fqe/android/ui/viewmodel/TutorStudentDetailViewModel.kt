package com.fqe.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fqe.android.data.model.StudentSessionResponse
import com.fqe.android.data.model.TutorStudentDetailResponse
import com.fqe.android.data.repository.TutorRepository
import com.fqe.android.data.repository.TutorResult
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TutorStudentDetailTab {
    Details,
    Sessions
}

enum class TutorSessionRangeFilter {
    All,
    Today,
    Last5Days,
    Last7Days,
    Last15Days,
    Last1Month,
    Last2Months
}

data class TutorStudentDetailUiState(
    val loading: Boolean = true,
    val isRefreshing: Boolean = false,
    val student: TutorStudentDetailResponse? = null,
    val sessions: List<StudentSessionResponse> = emptyList(),
    val error: String? = null,
    val sessionsError: String? = null,
    val selectedTab: TutorStudentDetailTab = TutorStudentDetailTab.Details,
    val selectedSessionFilter: TutorSessionRangeFilter = TutorSessionRangeFilter.All
)

class TutorStudentDetailViewModel(
    private val studentId: Int,
    private val tutorRepository: TutorRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TutorStudentDetailUiState())
    val uiState: StateFlow<TutorStudentDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        fetchStudentDetail(showLoader = true, showRefreshing = false)
    }

    fun refresh() {
        fetchStudentDetail(
            showLoader = _uiState.value.student == null && _uiState.value.sessions.isEmpty(),
            showRefreshing = _uiState.value.student != null || _uiState.value.sessions.isNotEmpty()
        )
    }

    private fun fetchStudentDetail(showLoader: Boolean, showRefreshing: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loading = showLoader,
                    isRefreshing = showRefreshing,
                    error = null,
                    sessionsError = null
                )
            }

            supervisorScope {
                val studentDeferred = async { tutorRepository.getStudentDetail(studentId) }
                val sessionsDeferred = async { tutorRepository.getStudentSessions(studentId) }

                val studentResult = studentDeferred.await()
                val sessionsResult = sessionsDeferred.await()

                _uiState.update { current ->
                    current.copy(
                        loading = false,
                        isRefreshing = false,
                        student = (studentResult as? TutorResult.Success)?.data,
                        sessions = ((sessionsResult as? TutorResult.Success)?.data ?: emptyList())
                            .sortedByDescending { it.beginningDate.orEmpty() },
                        error = (studentResult as? TutorResult.Error)?.message,
                        sessionsError = (sessionsResult as? TutorResult.Error)?.message
                    )
                }
            }
        }
    }

    fun selectTab(tab: TutorStudentDetailTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun selectSessionFilter(filter: TutorSessionRangeFilter) {
        _uiState.update { it.copy(selectedSessionFilter = filter) }
    }
}

class TutorStudentDetailViewModelFactory(
    private val studentId: Int,
    private val tutorRepository: TutorRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TutorStudentDetailViewModel(studentId, tutorRepository) as T
    }
}