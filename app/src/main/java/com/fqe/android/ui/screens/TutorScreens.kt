package com.fqe.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.PersonAddAlt1
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fqe.android.data.model.StudentSessionResponse
import com.fqe.android.data.model.TutorStudentResponse
import com.fqe.android.data.model.TutorStudentDetailResponse
import com.fqe.android.ui.viewmodel.TutorHomeViewModel
import com.fqe.android.ui.viewmodel.TutorHomeUiState
import com.fqe.android.data.model.TutorProfileResponse
import com.fqe.android.ui.viewmodel.TutorEditableField
import com.fqe.android.ui.viewmodel.TutorProfileUiState
import com.fqe.android.ui.viewmodel.TutorProfileViewModel
import com.fqe.android.ui.viewmodel.TutorSessionRangeFilter
import com.fqe.android.ui.viewmodel.TutorStudentDetailTab
import com.fqe.android.ui.viewmodel.TutorStudentDetailUiState
import com.fqe.android.ui.viewmodel.TutorStudentDetailViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorHomeScreen(
    viewModel: TutorHomeViewModel,
    onOpenProfile: () -> Unit,
    onStudentClick: (TutorStudentResponse) -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.isAssignDialogOpen) {
        AssignStudentDialog(
            email = state.assignStudentEmail,
            isSubmitting = state.assignInProgress,
            error = state.assignError,
            onEmailChange = viewModel::onAssignStudentEmailChange,
            onDismiss = viewModel::dismissAssignDialog,
            onConfirm = viewModel::assignStudent
        )
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = viewModel::refreshStudents,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Column {
                    Text(
                        text = "Panel del tutor",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Consulta tu perfil y sigue de cerca a tus alumnos desde una vista elegante, clara y lista para crecer.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Tu vitrina profesional",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Revisa email, pais, genero, edad, grado y el numero total de estudiantes asociados.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Badge {
                                Text(state.students.size.toString())
                            }
                            Text(
                                text = if (state.students.size == 1) "alumno asignado" else "alumnos asignados",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onOpenProfile,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ver mis datos")
                        }
                    }
                }

                StudentsShowcaseSection(
                    state = state,
                    onRetry = viewModel::loadStudents,
                    onOpenAssignDialog = viewModel::openAssignDialog,
                    onDismissFeedback = viewModel::clearFeedbackMessage,
                    onStudentClick = onStudentClick
                )

                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesion")
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StudentHomeScreen(
    viewModel: TutorStudentDetailViewModel,
    onOpenProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val student = state.student
    val tutor = student?.tutor
    val totalPlayedMinutes = state.sessions.sumOf { session ->
        sessionDurationMinutes(session.beginningDate, session.endDate)
    }
    val completedLevelsCount = state.sessions.sumOf { session ->
        session.levelResults.count { it.completed == true }
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = viewModel::refresh,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Column {
                    Text(
                        text = "Panel del estudiante",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Consulta tus datos, revisa a tu tutor asignado y sigue tu historial de juego desde una sola vista.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                when {
                    state.loading && student == null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    student == null -> {
                        ErrorCard(
                            message = state.error ?: "No se pudo cargar tu perfil.",
                            onRetry = viewModel::load
                        )
                    }

                    else -> {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    text = student.name.orEmpty().ifBlank { "Estudiante" },
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Accede a tus datos personales, la informacion de tu tutor y el detalle completo de tus sesiones.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Badge {
                                        Text(state.sessions.size.toString())
                                    }
                                    Text(
                                        text = if (state.sessions.size == 1) "sesion registrada" else "sesiones registradas",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = onOpenProfile,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Ver mis datos")
                                }
                            }
                        }

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(22.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Tutor asignado",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )

                                if (tutor == null) {
                                    Text(
                                        text = "Aun no tienes un tutor asociado.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    ReadOnlyInfoRow(label = "Nombre", value = tutor.name.orEmpty())
                                    ReadOnlyInfoRow(label = "Email", value = tutor.email.orEmpty())
                                    ReadOnlyInfoRow(label = "Pais", value = tutor.country.orEmpty())
                                }
                            }
                        }

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(22.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Resumen de sesiones",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                ReadOnlyInfoRow(
                                    label = "Tiempo total jugado",
                                    value = formatTotalPlayedTime(totalPlayedMinutes)
                                )
                                ReadOnlyInfoRow(
                                    label = "Niveles completados",
                                    value = completedLevelsCount.toString()
                                )
                                ReadOnlyInfoRow(
                                    label = "Ultima sesion",
                                    value = state.sessions.firstOrNull()?.beginningDate?.let(::formatSessionStartDateTime)
                                        ?: "Sin sesiones"
                                )
                            }
                        }

                        state.sessionsError?.let { message ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = message,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesion")
                }
            }
        }
    }
}

@Composable
private fun StudentsShowcaseSection(
    state: TutorHomeUiState,
    onRetry: () -> Unit,
    onOpenAssignDialog: () -> Unit,
    onDismissFeedback: () -> Unit,
    onStudentClick: (TutorStudentResponse) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = "Tus alumnos",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Toca cualquier tarjeta para abrir su siguiente vista.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.padding(start = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.School,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(state.students.size.toString(), fontWeight = FontWeight.SemiBold)
                        }
                    }

                    FilledIconButton(
                        onClick = onOpenAssignDialog,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PersonAddAlt1,
                            contentDescription = "Asignar alumno"
                        )
                    }
                }
            }

            state.feedbackMessage?.let { message ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Check, contentDescription = null)
                            Text(message, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        }

                        IconButton(onClick = onDismissFeedback) {
                            Icon(Icons.Outlined.Close, contentDescription = "Cerrar mensaje")
                        }
                    }
                }
            }

            when {
                state.loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null -> {
                    ErrorCard(message = state.error, onRetry = onRetry)
                }

                state.students.isEmpty() -> {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Todavia no tienes alumnos asignados.", fontWeight = FontWeight.SemiBold)
                            Text(
                                "Cuando empieces a asignarlos, apareceran aqui con una vista resumida.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        state.students.forEach { student ->
                            StudentHighlightCard(
                                student = student,
                                onClick = { onStudentClick(student) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentHighlightCard(
    student: TutorStudentResponse,
    onClick: () -> Unit
) {
    val neurodivergency = student.neurodivergency?.trim().orEmpty()
    val isNeurodivergent = neurodivergency.isNotBlank() && !neurodivergency.equals("NULL", ignoreCase = true)
    val normalizedGenre = student.genre?.trim()?.uppercase()
    val cardColor = when (normalizedGenre) {
        "M" -> Color(0xFFEAF4FF)
        "F" -> Color(0xFFFFEEF6)
        else -> MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val avatarColor = when (normalizedGenre) {
        "M" -> Color(0xFFD5E9FF)
        "F" -> Color(0xFFFFDDEE)
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(26.dp),
        color = cardColor,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = avatarColor,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Face, contentDescription = null)
                        }
                    }

                    Column {
                        Text(
                            text = student.name.orEmpty().ifBlank { "Alumno sin nombre" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Edad ${student.age}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = onClick,
                    label = { Text(if (isNeurodivergent) "Neurodivergente" else "No neurodivergente") },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Psychology,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isNeurodivergent) {
                            MaterialTheme.colorScheme.tertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.secondaryContainer
                        }
                    )
                )

                if (isNeurodivergent) {
                    AssistChip(
                        onClick = onClick,
                        label = { Text(neurodivergency) }
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TutorStudentDetailScreen(
    viewModel: TutorStudentDetailViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedSession by remember { mutableStateOf<StudentSessionResponse?>(null) }

    selectedSession?.let { session ->
        SessionHistoryDialog(
            session = session,
            sessionNumber = getStudentSessionNumber(session, state.sessions),
            onDismiss = { selectedSession = null }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(state.student?.name.orEmpty().ifBlank { "Alumno" })
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.loading && state.student == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.student == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        ErrorCard(
                            message = state.error ?: "No se pudo cargar el alumno.",
                            onRetry = viewModel::load
                        )
                    }
                }

                else -> {
                    TutorStudentDetailContent(
                        state = state,
                        modifier = Modifier.fillMaxSize(),
                        onRetry = viewModel::load,
                        onTabSelected = viewModel::selectTab,
                        onSessionFilterSelected = viewModel::selectSessionFilter,
                        onSessionClick = { selectedSession = it }
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StudentProfileScreen(
    viewModel: TutorStudentDetailViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedSession by remember { mutableStateOf<StudentSessionResponse?>(null) }

    selectedSession?.let { session ->
        SessionHistoryDialog(
            session = session,
            sessionNumber = getStudentSessionNumber(session, state.sessions),
            onDismiss = { selectedSession = null }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Mi perfil")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Cerrar sesion")
                    }
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.loading && state.student == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.student == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        ErrorCard(
                            message = state.error ?: "No se pudo cargar tu perfil.",
                            onRetry = viewModel::load
                        )
                    }
                }

                else -> {
                    TutorStudentDetailContent(
                        state = state,
                        modifier = Modifier.fillMaxSize(),
                        onRetry = viewModel::load,
                        onTabSelected = viewModel::selectTab,
                        onSessionFilterSelected = viewModel::selectSessionFilter,
                        onSessionClick = { selectedSession = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun TutorStudentDetailContent(
    state: TutorStudentDetailUiState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    onTabSelected: (TutorStudentDetailTab) -> Unit,
    onSessionFilterSelected: (TutorSessionRangeFilter) -> Unit,
    onSessionClick: (StudentSessionResponse) -> Unit
) {
    val student = state.student ?: return
    val filteredSessions = filterSessionsByRange(state.sessions, state.selectedSessionFilter)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(
                selected = state.selectedTab == TutorStudentDetailTab.Details,
                onClick = { onTabSelected(TutorStudentDetailTab.Details) },
                label = { Text("Datos") }
            )
            FilterChip(
                selected = state.selectedTab == TutorStudentDetailTab.Sessions,
                onClick = { onTabSelected(TutorStudentDetailTab.Sessions) },
                label = { Text("Sesiones") }
            )
        }

        StudentHeroCard(
            student = student,
            selectedTab = state.selectedTab,
            sessions = filteredSessions
        )

        if (state.selectedTab == TutorStudentDetailTab.Sessions) {
            SessionFilterSelector(
                selectedFilter = state.selectedSessionFilter,
                onFilterSelected = onSessionFilterSelected
            )
        }

        when (state.selectedTab) {
            TutorStudentDetailTab.Details -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        StudentDataSection(student = student)
                    }
                    item {
                        StudentSupportSection(student = student)
                    }
                    state.error?.let { message ->
                        item {
                            ErrorCard(message = message, onRetry = onRetry)
                        }
                    }
                }
            }

            TutorStudentDetailTab.Sessions -> {
                when {
                    state.loading && state.sessions.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    state.sessionsError != null && state.sessions.isEmpty() -> {
                        ErrorCard(message = state.sessionsError, onRetry = onRetry)
                    }

                    state.sessions.isEmpty() -> {
                        EmptySessionsCard()
                    }

                    filteredSessions.isEmpty() -> {
                        EmptySessionsCard(
                            title = "No hay sesiones en este rango.",
                            description = "Prueba con otro filtro para ver mas actividad del alumno."
                        )
                    }

                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            state.sessionsError?.let { message ->
                                item {
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.errorContainer
                                    ) {
                                        Text(
                                            text = message,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.padding(14.dp)
                                        )
                                    }
                                }
                            }

                            filteredSessions.forEach { session ->
                                item(key = session.idSession) {
                                    SessionHistoryCard(
                                        session = session,
                                        sessionNumber = getStudentSessionNumber(session, state.sessions),
                                        onClick = { onSessionClick(session) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentHeroCard(
    student: TutorStudentDetailResponse,
    selectedTab: TutorStudentDetailTab,
    sessions: List<StudentSessionResponse>
) {
    val normalizedGenre = student.genre?.trim()?.uppercase()
    val containerColor = when (normalizedGenre) {
        "M" -> Color(0xFFE9F4FF)
        "F" -> Color(0xFFFFEEF6)
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    val playedLevelsCount = sessions.sumOf { it.levelResults.size }
    val completedLevelsCount = sessions.sumOf { session ->
        session.levelResults.count { it.completed == true }
    }
    val totalPlayedMinutes = sessions.sumOf { session ->
        sessionDurationMinutes(session.beginningDate, session.endDate)
    }
    val completionPercentage = if (playedLevelsCount == 0) {
        0
    } else {
        (completedLevelsCount * 100) / playedLevelsCount
    }
    val heroChipColors = AssistChipDefaults.assistChipColors(
        disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        disabledLabelColor = MaterialTheme.colorScheme.onSurface
    )

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = student.name.orEmpty().ifBlank { "Alumno sin nombre" },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedTab == TutorStudentDetailTab.Details) {
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = { Text("Edad ${student.age}") },
                        colors = heroChipColors
                    )
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = { Text(student.country.orEmpty().ifBlank { "Pais no disponible" }) },
                        colors = heroChipColors
                    )
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = {
                            Text(
                                if (student.neurodivergency.isNullOrBlank() || student.neurodivergency.equals("NULL", ignoreCase = true)) {
                                    "No neurodivergente"
                                } else {
                                    "Neurodivergente"
                                }
                            )
                        },
                        colors = heroChipColors
                    )
                } else {
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = { Text("${sessions.size} sesiones") },
                        colors = heroChipColors
                    )
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = { Text("${playedLevelsCount} niveles jugados") },
                        colors = heroChipColors
                    )
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = { Text(formatTotalPlayedTime(totalPlayedMinutes)) },
                        colors = heroChipColors
                    )
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = { Text("${completionPercentage}% completado") },
                        colors = heroChipColors
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentDataSection(student: TutorStudentDetailResponse) {
    SectionCard(title = "Datos del alumno") {
        ReadOnlyInfoRow(label = "Email", value = student.email.orEmpty())
        ReadOnlyInfoRow(label = "Genero", value = formatStudentGenre(student.genre))
        ReadOnlyInfoRow(label = "Pais", value = student.country.orEmpty())
        ReadOnlyInfoRow(label = "Fecha de registro", value = formatRegistrationDate(student.registrationDate))
    }
}

@Composable
private fun StudentSupportSection(student: TutorStudentDetailResponse) {
    SectionCard(title = "Acompanamiento") {
        ReadOnlyInfoRow(
            label = "Neurodivergencia",
            value = student.neurodivergency
                ?.takeUnless { it.equals("NULL", ignoreCase = true) }
                .orEmpty()
                .ifBlank { "No reportada" }
        )
        ReadOnlyInfoRow(label = "Tutor asignado", value = student.tutor?.name.orEmpty())
        ReadOnlyInfoRow(label = "Email del tutor", value = student.tutor?.email.orEmpty())
    }
}

@Composable
private fun EmptySessionsCard() {
    EmptySessionsCard(
        title = "Aun no hay sesiones registradas.",
        description = "Cuando el alumno juegue, aqui aparecera su historial con fecha, niveles jugados y duracion."
    )
}

@Composable
private fun EmptySessionsCard(
    title: String,
    description: String
) {
    ElevatedCard(shape = RoundedCornerShape(28.dp)) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(
                description,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SessionFilterSelector(
    selectedFilter: TutorSessionRangeFilter,
    onFilterSelected: (TutorSessionRangeFilter) -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Filtrar sesiones",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TutorSessionRangeFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { onFilterSelected(filter) },
                        label = { Text(getSessionFilterLabel(filter)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionHistoryCard(
    session: StudentSessionResponse,
    sessionNumber: Int,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatSessionStartDateTime(session.beginningDate),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sesion #$sessionNumber",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = onClick,
                    label = { Text("Inicio ${formatSessionStartTime(session.beginningDate)}") },
                    leadingIcon = {
                        Icon(Icons.Outlined.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                )
                AssistChip(
                    onClick = onClick,
                    label = { Text("${session.levelResults.size} niveles") },
                    leadingIcon = {
                        Icon(Icons.Outlined.SportsEsports, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                )
                AssistChip(
                    onClick = onClick,
                    label = { Text(formatSessionDuration(session.beginningDate, session.endDate)) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Timer, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                )
            }
        }
    }
}

@Composable
private fun SessionHistoryDialog(
    session: StudentSessionResponse,
    sessionNumber: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Outlined.Schedule, contentDescription = null)
        },
        title = {
            Text("Sesion #$sessionNumber")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Inicio: ${formatSessionStartDateTime(session.beginningDate)}")
                Text("Fin: ${formatSessionEndDateTime(session.endDate)}")
                Text("Duracion: ${formatSessionDuration(session.beginningDate, session.endDate)}")
                Text("Niveles jugados: ${session.levelResults.size}")
                Text("Niveles completados: ${session.levelResults.count { it.completed == true }}")
                session.device?.takeIf { it.isNotBlank() }?.let {
                    Text("Dispositivo: $it")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TutorProfileScreen(
    viewModel: TutorProfileViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Perfil del tutor") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Cerrar sesion")
                    }
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = viewModel::refreshProfile,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.loading && state.profile == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                TutorProfileContent(
                    state = state,
                    modifier = Modifier.fillMaxSize(),
                    onRetry = viewModel::loadProfile,
                    onEditField = viewModel::beginEditing,
                    onCancelEdit = viewModel::cancelEditing,
                    onSaveField = viewModel::saveCurrentField,
                    onCountryChange = viewModel::onCountryChange,
                    onGenderChange = viewModel::onGenderChange,
                    onAgeChange = viewModel::onAgeChange,
                    onGradeChange = viewModel::onGradeChange
                )
            }
        }
    }
}

@Composable
private fun TutorProfileContent(
    state: TutorProfileUiState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    onEditField: (TutorEditableField) -> Unit,
    onCancelEdit: () -> Unit,
    onSaveField: () -> Unit,
    onCountryChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onGradeChange: (String) -> Unit
) {
    val profile = state.profile

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (profile == null) {
            ErrorCard(
                message = state.error ?: "No se pudo cargar el perfil.",
                onRetry = onRetry
            )
            return@Column
        }

        ElevatedCard(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tu perfil profesional en una sola vista.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = { Text("Students: ${profile.students.size}") },
                        colors = AssistChipDefaults.assistChipColors(
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = { Text("Tutor #${profile.idTutor}") },
                        colors = AssistChipDefaults.assistChipColors(
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        state.successMessage?.let {
            Text(it, color = Color(0xFF0B6B3A))
        }

        SectionCard(title = "Datos editables") {
            EditableInfoRow(
                label = "Country",
                value = state.countryInput,
                isEditing = state.editingField == TutorEditableField.Country,
                error = state.countryError,
                saving = state.saving,
                onEdit = { onEditField(TutorEditableField.Country) },
                onCancel = onCancelEdit,
                onSave = onSaveField
            ) {
                OutlinedTextField(
                    value = state.countryInput,
                    onValueChange = onCountryChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Country") },
                    supportingText = { Text("Codigo ISO de 2 letras") },
                    singleLine = true
                )
            }

            EditableInfoRow(
                label = "Gender",
                value = state.genderInput.ifBlank { "No definido" },
                isEditing = state.editingField == TutorEditableField.Gender,
                error = state.genderError,
                saving = state.saving,
                onEdit = { onEditField(TutorEditableField.Gender) },
                onCancel = onCancelEdit,
                onSave = onSaveField
            ) {
                OptionSelector(
                    options = TutorProfileViewModel.genderOptions,
                    selected = state.genderInput,
                    onSelect = onGenderChange
                )
            }

            EditableInfoRow(
                label = "Age",
                value = state.ageInput.ifBlank { "No definida" },
                isEditing = state.editingField == TutorEditableField.Age,
                error = state.ageError,
                saving = state.saving,
                onEdit = { onEditField(TutorEditableField.Age) },
                onCancel = onCancelEdit,
                onSave = onSaveField
            ) {
                OutlinedTextField(
                    value = state.ageInput,
                    onValueChange = onAgeChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            EditableInfoRow(
                label = "Grade",
                value = state.gradeInput.ifBlank { "No definido" },
                isEditing = state.editingField == TutorEditableField.Grade,
                error = state.gradeError,
                saving = state.saving,
                onEdit = { onEditField(TutorEditableField.Grade) },
                onCancel = onCancelEdit,
                onSave = onSaveField
            ) {
                OptionSelector(
                    options = TutorProfileViewModel.gradeOptions,
                    selected = state.gradeInput,
                    onSelect = onGradeChange
                )
            }
        }

        SectionCard(title = "Solo lectura") {
            ReadOnlyInfoRow(label = "Email", value = profile.email)
            ReadOnlyInfoRow(label = "Registration date", value = formatRegistrationDate(profile.registrationDate))
            ReadOnlyInfoRow(label = "Students", value = profile.students.size.toString())
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    ElevatedCard(shape = RoundedCornerShape(28.dp)) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            content()
        }
    }
}

@Composable
private fun ReadOnlyInfoRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value.ifBlank { "No disponible" }, style = MaterialTheme.typography.bodyLarge)
        HorizontalDivider()
    }
}

@Composable
private fun EditableInfoRow(
    label: String,
    value: String,
    isEditing: Boolean,
    error: String?,
    saving: Boolean,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    editorContent: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.bodyLarge)
            }

            if (!isEditing) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Editar $label")
                }
            }
        }

        if (isEditing) {
            editorContent()
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onCancel, enabled = !saving) {
                    Icon(Icons.Outlined.Close, contentDescription = "Cancelar")
                }
                IconButton(onClick = onSave, enabled = !saving) {
                    if (saving) {
                        CircularProgressIndicator(modifier = Modifier.width(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Outlined.Check, contentDescription = "Guardar")
                    }
                }
            }
        }

        HorizontalDivider()
    }
}

@Composable
private fun OptionSelector(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.chunked(2).forEach { rowOptions ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowOptions.forEach { option ->
                    FilterChip(
                        selected = selected == option,
                        onClick = { onSelect(option) },
                        label = { Text(option) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
    ElevatedCard(shape = RoundedCornerShape(28.dp)) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(message, color = MaterialTheme.colorScheme.error)
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

private fun formatRegistrationDate(value: String?): String {
    if (value.isNullOrBlank()) return "No disponible"
    return value.replace('T', ' ').removeSuffix("Z")
}

private fun formatStudentGenre(value: String?): String {
    return when (value?.trim()?.uppercase()) {
        "M" -> "Masculino"
        "F" -> "Femenino"
        else -> value.orEmpty().ifBlank { "No disponible" }
    }
}

private fun parseBackendDate(value: String?): Date? {
    if (value.isNullOrBlank()) return null

    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss"
    )

    return patterns.firstNotNullOfOrNull { pattern ->
        runCatching {
            SimpleDateFormat(pattern, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.parse(value)
        }.getOrNull()
    }
}

private fun formatSessionStartDateTime(value: String?): String {
    val date = parseBackendDate(value) ?: return "Fecha no disponible"
    return SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "MX")).format(date)
}

private fun formatSessionStartTime(value: String?): String {
    val date = parseBackendDate(value) ?: return "sin hora"
    return SimpleDateFormat("HH:mm", Locale("es", "MX")).format(date)
}

private fun formatSessionEndDateTime(value: String?): String {
    val date = parseBackendDate(value) ?: return "En curso"
    return SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "MX")).format(date)
}

private fun formatSessionDuration(beginningDate: String?, endDate: String?): String {
    if (parseBackendDate(beginningDate) == null) {
        return "Duracion no disponible"
    }
    if (parseBackendDate(endDate) == null) {
        return "En curso"
    }
    val totalMinutes = sessionDurationMinutes(beginningDate, endDate)
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes} min"
    }
}

private fun formatTotalPlayedTime(totalMinutes: Long): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        totalMinutes == 0L -> "0 h jugadas"
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}m jugadas"
        hours > 0 -> "${hours} h jugadas"
        else -> "${minutes} min jugados"
    }
}

private fun sessionDurationMinutes(beginningDate: String?, endDate: String?): Long {
    val start = parseBackendDate(beginningDate) ?: return 0L
    val end = parseBackendDate(endDate) ?: return 0L
    val diffMillis = (end.time - start.time).coerceAtLeast(0)
    return TimeUnit.MILLISECONDS.toMinutes(diffMillis)
}

@Composable
private fun AssignStudentDialog(
    email: String,
    isSubmitting: Boolean,
    error: String?,
    onEmailChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!isSubmitting) {
                onDismiss()
            }
        },
        icon = {
            Icon(Icons.Outlined.PersonAddAlt1, contentDescription = null)
        },
        title = {
            Text("Asignar alumno")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Ingresa el correo del alumno y lo vincularemos a tu cuenta de tutor.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Email del alumno") },
                    singleLine = true,
                    enabled = !isSubmitting,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Asignar")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSubmitting
            ) {
                Text("Cancelar")
            }
        }
    )
}

private fun getStudentSessionNumber(
    session: StudentSessionResponse,
    orderedSessions: List<StudentSessionResponse>
): Int {
    val sessionIndex = orderedSessions.indexOfFirst { it.idSession == session.idSession }
    return if (sessionIndex >= 0) {
        orderedSessions.size - sessionIndex
    } else {
        1
    }
}

private fun getSessionFilterLabel(filter: TutorSessionRangeFilter): String {
    return when (filter) {
        TutorSessionRangeFilter.All -> "Todas"
        TutorSessionRangeFilter.Today -> "Hoy"
        TutorSessionRangeFilter.Last5Days -> "5 dias"
        TutorSessionRangeFilter.Last7Days -> "7 dias"
        TutorSessionRangeFilter.Last15Days -> "15 dias"
        TutorSessionRangeFilter.Last1Month -> "1 mes"
        TutorSessionRangeFilter.Last2Months -> "2 meses"
    }
}

private fun filterSessionsByRange(
    sessions: List<StudentSessionResponse>,
    filter: TutorSessionRangeFilter,
    now: Date = Date()
): List<StudentSessionResponse> {
    if (filter == TutorSessionRangeFilter.All) {
        return sessions
    }

    val todayStart = Calendar.getInstance().apply {
        time = now
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val lowerBound = when (filter) {
        TutorSessionRangeFilter.All -> Long.MIN_VALUE
        TutorSessionRangeFilter.Today -> todayStart.timeInMillis
        TutorSessionRangeFilter.Last5Days -> calendarDaysAgoStart(todayStart, 4)
        TutorSessionRangeFilter.Last7Days -> calendarDaysAgoStart(todayStart, 6)
        TutorSessionRangeFilter.Last15Days -> calendarDaysAgoStart(todayStart, 14)
        TutorSessionRangeFilter.Last1Month -> calendarMonthsAgoStart(todayStart, 1)
        TutorSessionRangeFilter.Last2Months -> calendarMonthsAgoStart(todayStart, 2)
    }
    val nowMillis = now.time

    return sessions.filter { session ->
        val sessionDate = parseBackendDate(session.beginningDate) ?: return@filter false
        val sessionMillis = sessionDate.time
        sessionMillis in lowerBound..nowMillis
    }
}

private fun calendarDaysAgoStart(reference: Calendar, daysAgo: Int): Long {
    return (reference.clone() as Calendar).apply {
        add(Calendar.DAY_OF_YEAR, -daysAgo)
    }.timeInMillis
}

private fun calendarMonthsAgoStart(reference: Calendar, monthsAgo: Int): Long {
    return (reference.clone() as Calendar).apply {
        add(Calendar.MONTH, -monthsAgo)
    }.timeInMillis
}