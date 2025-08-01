package io.github.reyx38.neuropulse.presentation.UiCommon

import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.reyx38.neuropulse.presentation.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeuroDrawerScaffold(
    title: String = "NeuroPulse",
    navHostController: NavHostController,
    uiImagen: String?,
    content: @Composable (PaddingValues) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf("home") }

    val drawerItems = listOf(
        DrawerItem("progress", "Progresión Semanal", Icons.Default.ShowChart),
        DrawerItem("reflections", "Reflexiones", Icons.Default.EditNote, Screen.ReflexionListScreen),
        DrawerItem("activities", "Actividades Diarias", Icons.Default.Games),
        DrawerItem("Sesiones", "Historial de sesiones", Icons.Default.History, Screen.Sesiones )
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                drawerContentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "NeuroPulse",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(start = 24.dp, bottom = 16.dp)
                    )

                    drawerItems.forEach { item ->
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            selected = selectedItem == item.id,
                            onClick = {
                                selectedItem = item.id
                                scope.launch {
                                    drawerState.close()
                                    item.screen?.let { navHostController.navigate(it) }
                                }
                            },
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .fillMaxWidth(),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                title,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Abrir menú",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { navHostController.navigate(Screen.UsuarioOptiones) }) {
                                if (uiImagen == null) {
                                    Icon(
                                        Icons.Default.AccountCircle,
                                        contentDescription = "Gestión usuarios",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                } else {
                                    val imageBytes = Base64.decode(uiImagen, Base64.DEFAULT)
                                    val bitmap = remember(uiImagen) {
                                        try {
                                            android.graphics.BitmapFactory.decodeByteArray(
                                                imageBytes,
                                                0,
                                                imageBytes.size
                                            )
                                                ?.asImageBitmap()
                                        } catch (e: Exception) {
                                            null
                                        }
                                    }

                                    bitmap?.let {
                                        Image(
                                            bitmap = it,
                                            contentDescription = "perfil",
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                        )
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                containerColor = MaterialTheme.colorScheme.background,
                content = { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        content(innerPadding)
                    }
                }
            )
        }
    )
}

data class DrawerItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val screen: Screen? = null
)

fun getFrase(): String {
    val frases = listOf(
        "Cuidar de tu mente es tan importante como cuidar de tu cuerpo.",
        "Cada pequeño paso cuenta en el camino hacia el bienestar.",
        "No estás solo, pedir ayuda es un signo de fortaleza.",
        "Tu salud mental es una prioridad, no una opción.",
        "Permítete descansar sin culpa, tu mente lo agradecerá.",
        "La tranquilidad comienza con una mente equilibrada.",
        "Aprender a decir no, es un acto de amor propio.",
        "Cada día es una nueva oportunidad para sanar.",
        "Acepta tus emociones, ellas no te definen.",
        "El autocuidado no es egoísmo, es supervivencia.",
        "Hablar de salud mental abre puertas a la comprensión.",
        "La resiliencia se construye con paciencia y amor hacia uno mismo.",
        "Transforma el silencio en palabras, y el miedo en valor.",
        "Tu progreso es único, no lo compares con nadie más.",
        "Cultiva pensamientos positivos, tu mente merece paz.",
    )
    return frases.random()
}
