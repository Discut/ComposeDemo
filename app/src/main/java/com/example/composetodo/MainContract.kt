package com.example.composetodo

import com.example.mvi.contract.UiEffect
import com.example.mvi.contract.UiEvent
import com.example.mvi.contract.UiState

internal data class MainState(
    val defaultPage: String = NavigationBarItems.Add.route,
    val navBarItems: List<NavigationBarItems>,
) : UiState

internal sealed interface MainEvent : UiEvent {
    data class ClickNavigationItem(val navigationItem: NavigationBarItems) : MainEvent
}

internal sealed interface MainEffect : UiEffect {
    data class NavigateTo(val route: String) : MainEffect
}