package com.example.composetodo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationBarItems(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Favorite : NavigationBarItems("Favorite", "Favorite", Icons.Filled.Favorite)
    object Add : NavigationBarItems("Add", "Add", Icons.Filled.Add)
}