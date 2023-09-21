package com.example.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.mvi.contract.*

/**
 * @ClassName ViewModelExtensions.java
 * @author usopp
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022年09月15日 16:49:00
 */


@Composable
fun <S : UiState, E : UiEvent, F : UiEffect> BaseViewModel<S, E, F>.CollectSideEffect(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    sideEffect: (suspend (sideEffect: F) -> Unit),
) {
    val sideEffectFlow = this.uiEffect
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(sideEffectFlow, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            sideEffectFlow.collect { sideEffect(it) }
        }
    }
}