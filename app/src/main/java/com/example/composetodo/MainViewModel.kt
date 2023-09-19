package com.example.composetodo

import com.example.mvi.BaseViewModel

internal class MainViewModel :
    BaseViewModel<MainActivityState, MainActivityEvent, MainActivityEffect>() {
    override fun initialState(): MainActivityState {
        return MainActivityState(
            navBarItems = listOf(
                NavigationBarItems.Add,
                NavigationBarItems.Favorite
            )
        )
    }

    override suspend fun handleEvent(
        event: MainActivityEvent,
        state: MainActivityState
    ): MainActivityState {
        return when (event) {
            is MainActivityEvent.ClickNavigationItem -> {
                sendEffect(
                    MainActivityEffect.NavigateTo(
                        event.navigationItem.route
                    )
                )
                state
            }
        }
    }

}