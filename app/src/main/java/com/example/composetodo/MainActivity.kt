package com.example.composetodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.composetodo.ui.theme.ComposeTodoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bottomNavigationBarItems = listOf(NavigationBarItems.Add, NavigationBarItems.Favorite)
        setContent {
            val navController = rememberNavController()
            ComposeTodoTheme {
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
                            bottomNavigationBarItems.forEach {
                                NavigationBarItem(
                                    icon = { Icon(it.icon, contentDescription = null) },
                                    label = { Text(text = it.title) },
                                    selected = navController.currentBackStackEntryAsState().value?.destination?.route == it.route,
                                    onClick = {
                                        navController.navigate(it.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTodoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                TodoScreen()
                NavigationBar(modifier = Modifier.wrapContentHeight()) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text("Likes") },
                        selected = true,
                        onClick = { }
                    )


                }
            }
        }
    }
}