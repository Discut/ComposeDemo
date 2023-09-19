package com.example.composetodo

import com.example.mvi.BaseViewModel

internal class MainViewModel :
    BaseViewModel<MainState, MainEvent, MainEffect>() {
    override fun initialState(): MainState {
        return MainState(
            navBarItems = listOf(
                NavigationBarItems.Add,
                NavigationBarItems.Favorite
            )
        )
    }

    override suspend fun handleEvent(
        event: MainEvent,
        state: MainState
    ): MainState {
        return when (event) {
            is MainEvent.ClickNavigationItem -> {
                sendEffect(
                    MainEffect.NavigateTo(
                        event.navigationItem.route
                    )
                )
                state
            }
        }
    }

}