package com.fqe.android.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fqe.android.ui.viewmodel.LoginViewModel
import com.fqe.android.ui.viewmodel.SignupViewModel

private data class AuthOption(
    val value: String,
    val label: String
)

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onGoToSignup: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AuthScreenLayout(
        title = "Iniciar sesion",
        subtitle = "Bienvenido."
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AuthSectionTitle(
                    title = "Tus credenciales",
                    supporting = "Usa el correo con el que te registraste para recuperar tu sesion."
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo") },
                    leadingIcon = { AuthIcon(Icons.Outlined.MailOutline) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp)
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contrasena") },
                    leadingIcon = { AuthIcon(Icons.Outlined.AutoAwesome) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp)
                )

                state.error?.let {
                    StatusBanner(message = it, isError = true)
                }

                Button(
                    onClick = viewModel::login,
                    enabled = !state.loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Entrar")
                    }
                }

                TextButton(
                    onClick = onGoToSignup,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("No tienes cuenta? Registrate")
                }
            }
        }
    }
}

@Composable
fun SignupScreen(
    viewModel: SignupViewModel,
    onBackToLogin: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val roleOptions = listOf(
        AuthOption("student", "Estudiante"),
        AuthOption("tutor", "Tutor")
    )
    val genreOptions = listOf(
        AuthOption("M", "Masculino"),
        AuthOption("F", "Femenino"),
        AuthOption("O", "Otro")
    )
    val countryOptions = listOf(
        AuthOption("MX", "Mexico"),
        AuthOption("US", "Estados Unidos"),
        AuthOption("ES", "Espana")
    )
    val neurodivergencyOptions = SignupViewModel.neurodivergencyOptions.map { value ->
        AuthOption(value, value)
    }
    val degreeOptions = SignupViewModel.degreeOptions.map { value ->
        AuthOption(value, value)
    }

    AuthScreenLayout(
        title = "Crear cuenta",
        subtitle = "Selecciona tu rol primero y completa solo las opciones validas para que el registro sea rapido y claro."
    ) {
        RoleSpotlightSelector(
            selectedRole = state.role,
            options = roleOptions,
            onRoleSelected = viewModel::onRoleChange
        )

        ElevatedCard(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.animateContentSize()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                AuthSectionTitle(
                    title = "Datos principales",
                    supporting = "Primero tu acceso, luego tu perfil. Todo con entradas guiadas."
                )

                OutlinedTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre") },
                    leadingIcon = { AuthIcon(Icons.Outlined.Person) },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp)
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo") },
                    leadingIcon = { AuthIcon(Icons.Outlined.MailOutline) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp)
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contrasena") },
                    leadingIcon = { AuthIcon(Icons.Outlined.AutoAwesome) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp)
                )

                SelectionBlock(
                    title = "Genero",
                    supporting = "Elige una sola opcion.",
                    options = genreOptions,
                    selectedValue = state.genre,
                    onSelect = viewModel::onGenreChange
                )

                SelectionBlock(
                    title = "Pais",
                    supporting = "Selecciona la region principal de tu cuenta.",
                    options = countryOptions,
                    selectedValue = state.country,
                    onSelect = viewModel::onCountryChange
                )

                AgeSelectorCard(
                    age = state.age,
                    role = state.role,
                    onAgeChange = { viewModel.onAgeChange(it.toInt()) }
                )

                if (state.role == "student") {
                    SelectionBlock(
                        title = "Neurodivergencia",
                        supporting = "Selecciona la opcion que mejor describa tu caso. Puedes elegir Ninguna.",
                        options = neurodivergencyOptions,
                        selectedValue = state.neurodivergency,
                        onSelect = viewModel::onNeurodivergencyChange,
                        leadingIcon = { AuthIcon(Icons.Outlined.Psychology) }
                    )
                } else {
                    SelectionBlock(
                        title = "Grado",
                        supporting = "Solo mostramos opciones validas para evitar capturas manuales.",
                        options = degreeOptions,
                        selectedValue = state.degree,
                        onSelect = viewModel::onDegreeChange,
                        leadingIcon = { AuthIcon(Icons.Outlined.WorkOutline) }
                    )
                }

                state.error?.let {
                    StatusBanner(message = it, isError = true)
                }

                state.successMessage?.let {
                    StatusBanner(message = it, isError = false)
                }

                Button(
                    onClick = {
                        viewModel.signup {
                            onBackToLogin()
                        }
                    },
                    enabled = !state.loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Registrarme")
                    }
                }

                TextButton(onClick = onBackToLogin, modifier = Modifier.fillMaxWidth()) {
                    Text("Ya tengo cuenta")
                }
            }
        }
    }
}

@Composable
private fun AuthScreenLayout(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f),
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(34.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            content()
        }
    }
}

@Composable
private fun RoleSpotlightSelector(
    selectedRole: String,
    options: List<AuthOption>,
    onRoleSelected: (String) -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Quiero registrarme como",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "El rol va primero para que el resto del formulario cambie de forma inmediata y solo muestre lo relevante.",
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { option ->
                    val isSelected = selectedRole == option.value
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onRoleSelected(option.value) }
                            .border(
                                width = 1.dp,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                },
                                shape = RoundedCornerShape(24.dp)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.surface
                        } else {
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.52f)
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            AuthIcon(
                                imageVector = if (option.value == "student") {
                                    Icons.Outlined.School
                                } else {
                                    Icons.Outlined.WorkOutline
                                }
                            )
                            Text(option.label, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = if (option.value == "student") {
                                    "Perfil personal y sesiones de juego."
                                } else {
                                    "Perfil profesional y acompanamiento."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionBlock(
    title: String,
    supporting: String,
    options: List<AuthOption>,
    selectedValue: String,
    onSelect: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            leadingIcon?.invoke()
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    text = supporting,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selectedValue == option.value,
                    onClick = { onSelect(option.value) },
                    label = { Text(option.label) }
                )
            }
        }
    }
}

@Composable
private fun AgeSelectorCard(
    age: Int,
    role: String,
    onAgeChange: (Float) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Edad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = if (role == "student") {
                            "Ajusta una edad apropiada para el perfil del estudiante."
                        } else {
                            "Ajusta la edad del perfil de tutor."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = age.toString(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Slider(
                value = age.toFloat(),
                valueRange = 1f..85f,
                onValueChange = onAgeChange
            )
        }
    }
}

@Composable
private fun StatusBanner(message: String, isError: Boolean) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isError) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            Color(0xFFDFF4E5)
        }
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            color = if (isError) {
                MaterialTheme.colorScheme.onErrorContainer
            } else {
                Color(0xFF0B6B3A)
            }
        )
    }
}

@Composable
private fun AuthSectionTitle(
    title: String,
    supporting: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Text(
            text = supporting,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AuthIcon(imageVector: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .padding(7.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Icon(
                imageVector = imageVector,
                contentDescription = null
            )
        }
    }
}

@Composable
fun HomeScreen(
    title: String,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar sesion")
        }
    }
}
