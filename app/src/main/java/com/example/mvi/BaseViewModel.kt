package com.example.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvi.contract.UiEffect
import com.example.mvi.contract.UiEvent
import com.example.mvi.contract.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : UiState, E : UiEvent, F : UiEffect> : ViewModel() {

    private val initialState: S by lazy { initialState() }

    protected abstract fun initialState(): S

    private val _uiState: MutableStateFlow<S> by lazy { MutableStateFlow(initialState) }

    val uiState: StateFlow<S> by lazy { _uiState }

    private val _uiEvent: MutableSharedFlow<E> = MutableSharedFlow()

    private val _uiEffect: MutableSharedFlow<F> = MutableSharedFlow()

    val uiEffect: Flow<F> = _uiEffect

    init {
        subscribeEvents()
    }

    protected abstract suspend fun handleEvent(event: E, state: S): S?

    /**
     * 收集事件
     */
    private fun subscribeEvents() {
        //
        viewModelScope.launch {
            _uiEvent.collect {
                reduceEvent(_uiState.value, it)
            }
        }
    }

    /**
     * 发送事件
     */
    fun sendEvent(event: E) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    /**
     * 发送effect
     */
    protected fun sendEffect(effect: F) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }


    private fun sendState(newState: S.() -> S) {
        _uiState.value = uiState.value.newState()
    }

    /**
     * 处理事件，更新状态
     * @param state S
     * @param event E
     */
    private fun reduceEvent(state: S, event: E) {
        viewModelScope.launch {
            handleEvent(event, state)?.let { newState -> sendState { newState } }
        }
    }


}