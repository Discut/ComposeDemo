package com.example.composetodo

import com.example.mvi.contract.*


internal data class TodoState(
    val isShowAddDialog: Boolean = false,
    val isLoading: Boolean = false,
    val todoList: List<Todo> = listOf(),
) : UiState

internal sealed interface TodoEvent : UiEvent {
    data class ShowData(val items: List<Todo>) : TodoEvent
    data class OnChangeDialogState(val show: Boolean) : TodoEvent
    data class AddNewItem(val text: String) : TodoEvent
    data class OnItemCheckedChanged(val index: Int, val isChecked: Boolean) : TodoEvent
}

internal sealed interface TodoEffect : UiEffect {
    // 已完成
    data class Completed(val text: String) : TodoEffect

}


data class Todo(
    val isChecked: Boolean,
    val text: String,
)