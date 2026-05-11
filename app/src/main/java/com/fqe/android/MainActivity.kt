package com.fqe.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.fqe.android.data.network.RetrofitProvider
import com.fqe.android.data.repository.AuthRepository
import com.fqe.android.data.repository.TutorRepository
import com.fqe.android.data.session.SessionStore
import com.fqe.android.ui.navigation.AppNav

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContainer = AppContainer(
            authRepository = AuthRepository(RetrofitProvider.authApi),
            tutorRepository = TutorRepository(
                tutorApi = RetrofitProvider.tutorApi,
                sessionStore = SessionStore(applicationContext)
            ),
            sessionStore = SessionStore(applicationContext)
        )

        setContent {
            MaterialTheme {
                Surface {
                    AppNav(container = appContainer)
                }
            }
        }
    }
}

data class AppContainer(
    val authRepository: AuthRepository,
    val tutorRepository: TutorRepository,
    val sessionStore: SessionStore
)
