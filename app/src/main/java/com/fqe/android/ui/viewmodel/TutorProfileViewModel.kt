package com.fqe.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fqe.android.data.model.TutorProfileResponse
import com.fqe.android.data.model.UpdateTutorProfileRequest
import com.fqe.android.data.repository.TutorRepository
import com.fqe.android.data.repository.TutorResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class TutorEditableField {
    Country,
    Gender,
    Age,
    Grade
}

data class TutorProfileUiState(
    val loading: Boolean = true,
    val isRefreshing: Boolean = false,
    val saving: Boolean = false,
    val profile: TutorProfileResponse? = null,
    val editingField: TutorEditableField? = null,
    val countryInput: String = "",
    val genderInput: String = "",
    val ageInput: String = "",
    val gradeInput: String = "",
    val countryError: String? = null,
    val genderError: String? = null,
    val ageError: String? = null,
    val gradeError: String? = null,
    val error: String? = null,
    val successMessage: String? = null
)

class TutorProfileViewModel(
    private val tutorRepository: TutorRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TutorProfileUiState())
    val uiState: StateFlow<TutorProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        fetchProfile(showLoader = true, showRefreshing = false)
    }

    fun refreshProfile() {
        fetchProfile(
            showLoader = _uiState.value.profile == null,
            showRefreshing = _uiState.value.profile != null
        )
    }

    private fun fetchProfile(showLoader: Boolean, showRefreshing: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                loading = showLoader,
                isRefreshing = showRefreshing,
                error = null,
                successMessage = null
            )

            when (val result = tutorRepository.getTutorProfile()) {
                is TutorResult.Success -> {
                    _uiState.value = buildStateFromProfile(result.data).copy(
                        loading = false,
                        isRefreshing = false
                    )
                }
                is TutorResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        isRefreshing = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun beginEditing(field: TutorEditableField) {
        _uiState.value = _uiState.value.copy(
            editingField = field,
            error = null,
            successMessage = null,
            countryError = null,
            genderError = null,
            ageError = null,
            gradeError = null
        )
    }

    fun cancelEditing() {
        val profile = _uiState.value.profile ?: return
        _uiState.value = buildStateFromProfile(profile).copy(
            loading = false,
            successMessage = null
        )
    }

    fun onCountryChange(value: String) {
        _uiState.value = _uiState.value.copy(
            countryInput = value.take(2).uppercase(),
            countryError = null,
            error = null,
            successMessage = null
        )
    }

    fun onGenderChange(value: String) {
        _uiState.value = _uiState.value.copy(
            genderInput = value,
            genderError = null,
            error = null,
            successMessage = null
        )
    }

    fun onAgeChange(value: String) {
        _uiState.value = _uiState.value.copy(
            ageInput = value.filter(Char::isDigit).take(3),
            ageError = null,
            error = null,
            successMessage = null
        )
    }

    fun onGradeChange(value: String) {
        _uiState.value = _uiState.value.copy(
            gradeInput = value,
            gradeError = null,
            error = null,
            successMessage = null
        )
    }

    fun saveCurrentField() {
        val currentState = _uiState.value
        val profile = currentState.profile ?: return
        val validation = validateInputs(currentState)

        if (!validation.isValid) {
            _uiState.value = currentState.copy(
                countryError = validation.countryError,
                genderError = validation.genderError,
                ageError = validation.ageError,
                gradeError = validation.gradeError,
                error = "Revisa los datos antes de guardar.",
                successMessage = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(
                saving = true,
                error = null,
                successMessage = null
            )

            val request = UpdateTutorProfileRequest(
                name = profile.name,
                email = profile.email,
                country = currentState.countryInput.trim().uppercase(),
                gender = normalizeGenderForApi(currentState.genderInput),
                age = currentState.ageInput.toIntOrNull(),
                grade = normalizeGradeForApi(currentState.gradeInput)
            )

            when (val result = tutorRepository.updateTutorProfile(request)) {
                is TutorResult.Success -> {
                    _uiState.value = buildStateFromProfile(result.data).copy(
                        loading = false,
                        saving = false,
                        successMessage = "Dato actualizado correctamente."
                    )
                }
                is TutorResult.Error -> {
                    _uiState.value = currentState.copy(
                        saving = false,
                        error = result.message
                    )
                }
            }
        }
    }

    private fun buildStateFromProfile(profile: TutorProfileResponse): TutorProfileUiState {
        return TutorProfileUiState(
            profile = profile,
            countryInput = profile.country.trim().uppercase(),
            genderInput = normalizeGenderForUi(profile.gender),
            ageInput = profile.age?.toString().orEmpty(),
            gradeInput = normalizeGradeForUi(profile.grade)
        )
    }

    private fun validateInputs(state: TutorProfileUiState): ValidationResult {
        val country = state.countryInput.trim().uppercase()
        val age = state.ageInput.toIntOrNull()
        val gender = state.genderInput.trim()
        val grade = state.gradeInput.trim()

        return ValidationResult(
            countryError = if (!country.matches(Regex("^[A-Z]{2}$"))) {
                "Country debe tener 2 letras ISO, por ejemplo MX."
            } else {
                null
            },
            genderError = if (gender !in genderOptions) {
                "Genero invalido. Usa Masculino, Femenino u Otro."
            } else {
                null
            },
            ageError = if (age == null || age !in 18..120) {
                "Age debe estar entre 18 y 120."
            } else {
                null
            },
            gradeError = if (grade !in gradeOptions) {
                "Grade invalido. Elige una opcion valida."
            } else {
                null
            }
        )
    }

    companion object {
        val genderOptions = listOf("Masculino", "Femenino", "Otro")
        val gradeOptions = listOf(
            "Licenciatura",
            "Maestria",
            "Doctorado",
            "Post Doctorado",
            "Padre o Madre"
        )

        fun normalizeGenderForUi(value: String?): String {
            return when (value?.trim()?.uppercase()) {
                "M", "MASCULINO" -> "Masculino"
                "F", "FEMENINO" -> "Femenino"
                "O", "OTRO" -> "Otro"
                else -> value?.trim().orEmpty()
            }
        }

        fun normalizeGenderForApi(value: String): String? {
            return when (value.trim()) {
                "Masculino" -> "Masculino"
                "Femenino" -> "Femenino"
                "Otro" -> "Otro"
                else -> null
            }
        }

        fun normalizeGradeForUi(value: String?): String {
            return when (value?.trim()?.lowercase()) {
                "licenciatura" -> "Licenciatura"
                "maestria" -> "Maestria"
                "doctorado" -> "Doctorado"
                "post doctorado" -> "Post Doctorado"
                "padre o madre" -> "Padre o Madre"
                "otro" -> "Otro"
                else -> value?.trim().orEmpty()
            }
        }

        fun normalizeGradeForApi(value: String): String? {
            return when (value.trim()) {
                "Licenciatura" -> "licenciatura"
                "Maestria" -> "Maestria"
                "Doctorado" -> "Doctorado"
                "Post Doctorado" -> "Post Doctorado"
                "Padre o Madre" -> "Padre o Madre"
                else -> null
            }
        }
    }
}

private data class ValidationResult(
    val countryError: String? = null,
    val genderError: String? = null,
    val ageError: String? = null,
    val gradeError: String? = null
) {
    val isValid: Boolean
        get() = countryError == null && genderError == null && ageError == null && gradeError == null
}

class TutorProfileViewModelFactory(
    private val tutorRepository: TutorRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TutorProfileViewModel(tutorRepository) as T
    }
}