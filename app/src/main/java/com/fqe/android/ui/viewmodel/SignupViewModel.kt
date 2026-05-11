package com.fqe.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fqe.android.data.model.SignupRequest
import com.fqe.android.data.repository.AuthRepository
import com.fqe.android.data.repository.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SignupUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val age: Int = 5,
    val gender: String = "M",
    val country: String = "MX",
    val role: String = "student",
    val neurodivergency: String = "Ninguna",
    val degree: String = "Licenciatura",
    val loading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class SignupViewModel(
    private val repository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) { _uiState.value = _uiState.value.copy(name = value, error = null) }
    fun onEmailChange(value: String) { _uiState.value = _uiState.value.copy(email = value, error = null) }
    fun onPasswordChange(value: String) { _uiState.value = _uiState.value.copy(password = value, error = null) }
    fun onAgeChange(value: Int) { _uiState.value = _uiState.value.copy(age = value, error = null) }
    fun onGenreChange(value: String) { _uiState.value = _uiState.value.copy(gender = value, error = null) }
    fun onCountryChange(value: String) { _uiState.value = _uiState.value.copy(country = value, error = null) }
    fun onRoleChange(value: String) {
        val next = if (value == "tutor") 18 else 5
        _uiState.value = _uiState.value.copy(
            role = value,
            age = next,
            neurodivergency = if (value == "student") _uiState.value.neurodivergency else noNeurodivergencyOption,
            degree = if (value == "tutor") _uiState.value.degree else degreeOptions.first(),
            error = null
        )
    }
    fun onNeurodivergencyChange(value: String) { _uiState.value = _uiState.value.copy(neurodivergency = value, error = null) }
    fun onDegreeChange(value: String) { _uiState.value = _uiState.value.copy(degree = value, error = null) }

    fun signup(onSuccess: () -> Unit) {
        val state = _uiState.value
        val validationError = validate(state)
        if (validationError != null) {
            _uiState.value = state.copy(error = validationError)
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(loading = true, error = null)
            val request = SignupRequest(
                name = state.name.trim(),
                email = state.email.trim(),
                password = state.password,
                age = state.age,
                gender = state.gender,
                country = state.country,
                role = state.role,
                neurodivergency = if (state.role == "student" && state.neurodivergency != noNeurodivergencyOption) {
                    state.neurodivergency
                } else {
                    null
                },
                degree = if (state.role == "tutor") state.degree else null
            )

            when (val result = repository.signup(request)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        successMessage = "Registro exitoso, ahora inicia sesion"
                    )
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    private fun validate(state: SignupUiState): String? {
        if (state.name.isBlank()) return "El nombre es obligatorio"
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) return "Correo invalido"
        if (!Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$").matches(state.password)) {
            return "Password invalido: minimo 8, mayuscula, minuscula y numero"
        }
        if (state.age !in 1..85) return "La edad debe estar entre 1 y 85"
        if (state.gender !in genreOptions) return "Genero invalido"
        if (state.role !in listOf("student", "tutor")) return "Rol invalido"
        if (state.country !in countryOptions) return "Pais invalido"
        if (state.role == "student" && state.neurodivergency !in neurodivergencyOptions) return "Neurodivergencia invalida"
        if (state.role == "tutor" && state.degree !in degreeOptions) return "Grado invalido"
        return null
    }

    companion object {
        const val noNeurodivergencyOption = "Ninguna"

        val genreOptions = listOf("M", "F", "O")
        val countryOptions = listOf("MX", "US", "ES")
        val degreeOptions = listOf(
            "Licenciatura",
            "Maestria",
            "Doctorado",
            "Post Doctorado",
            "Padre o Madre"
        )
        val neurodivergencyOptions = listOf(
            noNeurodivergencyOption,
            "ADHD",
            "TEA",
            "Dislexia",
            "Discalculia",
            "Dispraxia"
        )
    }
}

class SignupViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignupViewModel(repository) as T
    }
}
