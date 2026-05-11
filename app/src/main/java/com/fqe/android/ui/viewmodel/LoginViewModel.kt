package com.fqe.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fqe.android.data.repository.AuthRepository
import com.fqe.android.data.repository.AuthResult
import com.fqe.android.data.session.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

class LoginViewModel(
    private val repository: AuthRepository,
    private val sessionStore: SessionStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, error = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun login() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "Email y contraseña son obligatorios")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(loading = true, error = null)
            when (val result = repository.login(state.email.trim(), state.password)) {
                is AuthResult.Success -> {
                    val token = result.data.token
                    sessionStore.saveToken(token)
                    _uiState.value = _uiState.value.copy(loading = false)
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
}

class LoginViewModelFactory(
    private val repository: AuthRepository,
    private val sessionStore: SessionStore
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(repository, sessionStore) as T
    }
}
