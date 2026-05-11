package com.fqe.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.fqe.android.ui.screens.HomeScreen
import com.fqe.android.ui.screens.LoginScreen
import com.fqe.android.ui.screens.SignupScreen
import com.fqe.android.ui.screens.StudentHomeScreen
import com.fqe.android.ui.screens.StudentProfileScreen
import com.fqe.android.ui.screens.TutorHomeScreen
import com.fqe.android.ui.screens.TutorProfileScreen
import com.fqe.android.ui.screens.TutorStudentDetailScreen
import com.fqe.android.ui.viewmodel.TutorHomeViewModel
import com.fqe.android.ui.viewmodel.TutorHomeViewModelFactory
import com.fqe.android.ui.viewmodel.LoginViewModel
import com.fqe.android.ui.viewmodel.LoginViewModelFactory
import com.fqe.android.ui.viewmodel.SessionViewModel
import com.fqe.android.ui.viewmodel.SessionViewModelFactory
import com.fqe.android.ui.viewmodel.SignupViewModel
import com.fqe.android.ui.viewmodel.SignupViewModelFactory
import com.fqe.android.ui.viewmodel.TutorProfileViewModel
import com.fqe.android.ui.viewmodel.TutorProfileViewModelFactory
import com.fqe.android.ui.viewmodel.TutorStudentDetailViewModel
import com.fqe.android.ui.viewmodel.TutorStudentDetailViewModelFactory
import com.fqe.android.AppContainer
import com.fqe.android.util.JwtUtils

@Composable
fun AppNav(
    container: AppContainer,
    navController: NavHostController = rememberNavController()
) {
    val sessionViewModel: SessionViewModel = viewModel(
        factory = SessionViewModelFactory(container.sessionStore)
    )
    val startDestination = sessionViewModel.startDestination.collectAsStateWithLifecycle()
    val sessionToken = container.sessionStore.tokenFlow.collectAsStateWithLifecycle(initialValue = null)
    val currentUserId = JwtUtils.extractUserId(sessionToken.value)

    LaunchedEffect(startDestination.value) {
        if (startDestination.value == null) return@LaunchedEffect
        val current = navController.currentDestination?.route
        val target = startDestination.value ?: "login"
        if (current != target) {
            navController.navigate(target) {
                popUpTo(0)
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            HomeScreen("Cargando sesion...", onLogout = {})
        }

        composable("login") {
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(container.authRepository, container.sessionStore)
            )
            LoginScreen(
                viewModel = loginViewModel,
                onGoToSignup = { navController.navigate("signup") }
            )
        }

        composable("signup") {
            val signupViewModel: SignupViewModel = viewModel(
                factory = SignupViewModelFactory(container.authRepository)
            )
            SignupScreen(
                viewModel = signupViewModel,
                onBackToLogin = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        composable("home/student") {
            if (currentUserId == null) {
                HomeScreen(title = "Cargando sesion...", onLogout = {})
            } else {
                val studentHomeViewModel: TutorStudentDetailViewModel = viewModel(
                    factory = TutorStudentDetailViewModelFactory(currentUserId, container.tutorRepository)
                )

                StudentHomeScreen(
                    viewModel = studentHomeViewModel,
                    onOpenProfile = { navController.navigate("student/profile") },
                    onLogout = {
                        sessionViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("home/student") { inclusive = true }
                        }
                    }
                )
            }
        }

        composable("home/tutor") {
            val tutorHomeViewModel: TutorHomeViewModel = viewModel(
                factory = TutorHomeViewModelFactory(container.tutorRepository)
            )

            TutorHomeScreen(
                viewModel = tutorHomeViewModel,
                onOpenProfile = { navController.navigate("tutor/profile") },
                onStudentClick = { student ->
                    navController.navigate("tutor/student/${student.idStudent}")
                },
                onLogout = {
                    sessionViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("home/tutor") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "tutor/student/{studentId}",
            arguments = listOf(navArgument("studentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: return@composable
            val studentDetailViewModel: TutorStudentDetailViewModel = viewModel(
                factory = TutorStudentDetailViewModelFactory(studentId, container.tutorRepository)
            )

            TutorStudentDetailScreen(
                viewModel = studentDetailViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("tutor/profile") {
            val tutorProfileViewModel: TutorProfileViewModel = viewModel(
                factory = TutorProfileViewModelFactory(container.tutorRepository)
            )

            TutorProfileScreen(
                viewModel = tutorProfileViewModel,
                onBack = { navController.popBackStack() },
                onLogout = {
                    sessionViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("student/profile") {
            if (currentUserId == null) {
                HomeScreen(title = "Cargando sesion...", onLogout = {})
            } else {
                val studentProfileViewModel: TutorStudentDetailViewModel = viewModel(
                    factory = TutorStudentDetailViewModelFactory(currentUserId, container.tutorRepository)
                )

                StudentProfileScreen(
                    viewModel = studentProfileViewModel,
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        sessionViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
