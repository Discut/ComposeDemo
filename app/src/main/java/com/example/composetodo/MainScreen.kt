package com.example.composetodo

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.base.flowbus.MainScreenToastEvent
import com.example.base.flowbus.observeEven
import com.example.composetodo.ui.component.CustomEdit
import com.example.mvi.collectSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searchText by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val navController = rememberNavController()
    val navControllerTop = rememberNavController()


    viewModel.collectSideEffect {
        when (it) {
            is MainEffect.NavigateTo -> navController.navigate(it.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }

            is MainEffect.OpenAbout -> navControllerTop.navigate("about") {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }

            else -> {}
        }
    }

    LocalLifecycleOwner.current.observeEven<MainScreenToastEvent> {
        Toast.makeText(context, "get msg is ${it.msg}", Toast.LENGTH_SHORT).show()
    }

    NavHost(navController = navControllerTop, startDestination = "main") {
        composable("main") {
            Scaffold(
                topBar = {
                    CustomEdit(
                        text = searchText,
                        onValueChange = {
                            searchText = it
                        },
                        startIcon = Icons.Filled.Search,
                        hint = "More...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 10.dp)
                            .height(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xBCE9E9E9))
                            .pointerInput(Unit) {
                                awaitPointerEventScope {
                                    awaitPointerEvent(PointerEventPass.Main)
                                    navControllerTop.navigate("about") {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                            .clickable { },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                },
                bottomBar = {
                    NavigationBar(modifier = Modifier.wrapContentHeight()) {
                        state.navBarItems.forEach {
                            NavigationBarItem(
                                icon = { Icon(it.icon, contentDescription = null) },
                                label = { Text(text = it.title) },
                                selected = navController.currentBackStackEntryAsState().value?.destination?.route == it.route,
                                onClick = { viewModel.sendEvent(MainEvent.ClickNavigationItem(it)) }
                            )
                        }
                    }
                }) {
                NavHost(
                    navController = navController,
                    startDestination = state.defaultPage,
                    modifier = Modifier.padding(it)
                ) {
                    composable(NavigationBarItems.Add.route) {
                        TodoScreen()
                    }
                    composable(NavigationBarItems.Favorite.route) {
                        AboutScreen()
                    }
                }
            }
        }
        composable("about") {
            AboutScreen2()
        }
    }

}