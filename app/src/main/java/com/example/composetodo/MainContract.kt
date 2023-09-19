package com.example.composetodo

import com.example.mvi.contract.UiEffect
import com.example.mvi.contract.UiEvent
import com.example.mvi.contract.UiState

internal data class MainActivityState(
    val defaultPage: String = NavigationBarItems.Add.route,
    val navBarItems: List<NavigationBarItems>,
) : UiState

internal sealed interface MainActivityEvent : UiEvent {
    data class ClickNavigationItem(val navigationItem: NavigationBarItems) : MainActivityEvent
}

internal sealed interface MainActivityEffect : UiEffect {
    data class NavigateTo(val route: String) : MainActivityEffect
}