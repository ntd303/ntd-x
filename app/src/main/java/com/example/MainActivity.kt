package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.components.XLogo
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SocialViewModel

class MainActivity : ComponentActivity() {

    private val socialViewModel: SocialViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val currentUser by socialViewModel.currentUser.collectAsStateWithLifecycle()

                if (currentUser == null) {
                    AuthScreen(
                        viewModel = socialViewModel,
                        onAuthSuccess = {
                            // Automatically logs in and initializes feed
                        }
                    )
                } else {
                    MainAppScaffold(
                        viewModel = socialViewModel,
                        currentUsername = currentUser!!.username
                    )
                }
            }
        }
    }
}

sealed class NavigationItem(val route: String, val label: String, val filledIcon: androidx.compose.ui.graphics.vector.ImageVector, val outlinedIcon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : NavigationItem("home", "Home", Icons.Default.Home, Icons.Outlined.Home)
    object Explore : NavigationItem("explore", "Explore", Icons.Default.Search, Icons.Outlined.Search)
    object Messages : NavigationItem("messages", "Messages", Icons.Default.Mail, Icons.Outlined.Mail)
    object Notifications : NavigationItem("notifications", "Notifications", Icons.Default.Notifications, Icons.Outlined.Notifications)
    object Profile : NavigationItem("profile", "Profile", Icons.Default.Person, Icons.Outlined.Person)
    object Admin : NavigationItem("admin", "Admin", Icons.Default.AdminPanelSettings, Icons.Outlined.AdminPanelSettings)
}

@Composable
fun MainAppScaffold(
    viewModel: SocialViewModel,
    currentUsername: String,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navigationItems = remember(currentUsername) {
        if (currentUsername == "admin") {
            listOf(
                NavigationItem.Home,
                NavigationItem.Explore,
                NavigationItem.Messages,
                NavigationItem.Notifications,
                NavigationItem.Profile,
                NavigationItem.Admin
            )
        } else {
            listOf(
                NavigationItem.Home,
                NavigationItem.Explore,
                NavigationItem.Messages,
                NavigationItem.Notifications,
                NavigationItem.Profile
            )
        }
    }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .background(DarkSurface)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val userState by viewModel.currentUser.collectAsStateWithLifecycle()
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(userState?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80")
                            .crossfade(true)
                            .build(),
                        contentDescription = "My Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .clickable {
                                viewModel.selectProfileUser(currentUsername)
                                navController.navigate(NavigationItem.Profile.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                    )

                    XLogo(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.CenterVertically)
                    )

                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Log out",
                            tint = TextSecondary
                        )
                    }
                }
                HorizontalDivider(color = DarkOutline)
            }
        },
        bottomBar = {
            Column {
                HorizontalDivider(color = DarkOutline)
                NavigationBar(
                    containerColor = DarkSurface,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    navigationItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (item == NavigationItem.Profile) {
                                    viewModel.selectProfileUser(currentUsername)
                                }
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.filledIcon else item.outlinedIcon,
                                    contentDescription = item.label,
                                    tint = if (isSelected) TwitterBlue else TextSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    color = if (isSelected) TwitterBlue else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = DarkSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_item_${item.route}")
                        )
                    }
                }
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onUserClick = { clickedUsername ->
                        viewModel.selectProfileUser(clickedUsername)
                        navController.navigate(NavigationItem.Profile.route)
                    },
                    onNavigateToRoute = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(NavigationItem.Explore.route) {
                ExploreScreen(
                    viewModel = viewModel,
                    onUserClick = { clickedUsername ->
                        viewModel.selectProfileUser(clickedUsername)
                        navController.navigate(NavigationItem.Profile.route)
                    }
                )
            }
            composable(NavigationItem.Messages.route) {
                MessagesScreen(
                    viewModel = viewModel,
                    onUserClick = { clickedUsername ->
                        viewModel.selectProfileUser(clickedUsername)
                        navController.navigate(NavigationItem.Profile.route)
                    }
                )
            }
            composable(NavigationItem.Notifications.route) {
                NotificationsScreen(
                    viewModel = viewModel,
                    onUserClick = { clickedUsername ->
                        viewModel.selectProfileUser(clickedUsername)
                        navController.navigate(NavigationItem.Profile.route)
                    }
                )
            }
            composable(NavigationItem.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onUserClick = { clickedUsername ->
                        viewModel.selectProfileUser(clickedUsername)
                        // Trigger simple navigate self to reload layout state
                        navController.navigate(NavigationItem.Profile.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(NavigationItem.Admin.route) {
                AdminDashboardScreen(
                    viewModel = viewModel,
                    onUserClick = { clickedUsername ->
                        viewModel.selectProfileUser(clickedUsername)
                        navController.navigate(NavigationItem.Profile.route)
                    }
                )
            }
        }
    }
}
