package com.fqe.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fqe.android.data.session.SessionStore
import com.fqe.android.util.JwtUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SessionViewModel(
    private val sessionStore: SessionStore
) : ViewModel() {
    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            sessionStore.tokenFlow.collectLatest { token ->
                val role = JwtUtils.extractRole(token)
                _startDestination.value = when (role) {
                    "student" -> "home/student"
                    "tutor" -> "home/tutor"
                    else -> "login"
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionStore.clearToken()
        }
    }
}

class SessionViewModelFactory(
    private val sessionStore: SessionStore
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SessionViewModel(sessionStore) as T
    }
}
