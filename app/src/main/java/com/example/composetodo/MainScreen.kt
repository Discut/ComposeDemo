package com.example.composetodo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mvi.collectSideEffect

@Composable
internal fun MainScreen(viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val navController = rememberNavController()

    viewModel.collectSideEffect {
        when (it) {
            is MainActivityEffect.NavigateTo -> navController.navigate(it.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Box(modifier = Modifier.weight(1f)) {
                NavHost(
                    navController = navController,
                    startDestination = NavigationBarItems.Add.route
                ) {
                    composable(NavigationBarItems.Add.route) {
                        TodoScreen()
                    }
                    composable(NavigationBarItems.Favorite.route) {
                        AboutScreen()
                    }
                }
            }
            NavigationBar(modifier = Modifier.wrapContentHeight()) {
                state.navBarItems.forEach {
                    NavigationBarItem(
                        icon = { Icon(it.icon, contentDescription = null) },
                        label = { Text(text = it.title) },
                        selected = navController.currentBackStackEntryAsState().value?.destination?.route == it.route,
                        onClick = { viewModel.sendEvent(MainActivityEvent.ClickNavigationItem(it)) }
                    )
                }
            }
        }
    }
}