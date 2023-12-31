package com.example.composetodo

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mvi.CollectSideEffect

@Composable
internal fun TodoScreen(
    viewModel: TodoViewModel = viewModel(),
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    viewModel.CollectSideEffect { effect ->
        when (effect) {
            is TodoEffect.Completed -> Toast.makeText(
                context,
                "${effect.text}已完成",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    when {
        state.isLoading -> ContentWithProgress()
        state.todoList.isNotEmpty() -> TodoListContent(
            state.todoList,
            state.isShowAddDialog,
            onItemCheckedChanged = { index, isChecked ->
                viewModel.sendEvent(TodoEvent.OnItemCheckedChanged(index, isChecked))
            },
            onAddButtonClick = { viewModel.sendEvent(TodoEvent.OnChangeDialogState(true)) },
            onDialogDismissClick = { viewModel.sendEvent(TodoEvent.OnChangeDialogState(false)) },
            onDialogOkClick = { text -> viewModel.sendEvent(TodoEvent.AddNewItem(text)) },
        )
    }
}

@Composable
private fun TodoListContent(
    todos: List<Todo>,
    isShowAddDialog: Boolean,
    onItemCheckedChanged: (Int, Boolean) -> Unit,
    onAddButtonClick: () -> Unit,
    onDialogDismissClick: () -> Unit,
    onDialogOkClick: (String) -> Unit,
) {
    Box {
        LazyColumn(content = {
            itemsIndexed(todos) { index, item ->
                TodoListItem(item = item, onItemCheckedChanged, index)
                if (index == todos.size - 1)
                    AddButton(onAddButtonClick)
            }

        })

        if (isShowAddDialog) {
            AddNewItemDialog(onDialogDismissClick, onDialogOkClick)
        }
    }
}

@Composable
private fun AddButton(
    onAddButtonClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.Center)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAddButtonClick
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddNewItemDialog(
    onDialogDismissClick: () -> Unit,
    onDialogOkClick: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = { },
        text = {
            TextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Blue,
                    disabledIndicatorColor = Color.Blue,
                    unfocusedIndicatorColor = Color.Blue,
                    //backgroundColor = Color.LightGray,
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { onDialogOkClick(text) },
                //colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)
            ) {
                Text(text = "Ok", style = TextStyle(color = Color.White, fontSize = 12.sp))
            }
        }, dismissButton = {
            Button(
                onClick = onDialogDismissClick,
                //colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)
            ) {
                Text(text = "Cancel", style = TextStyle(color = Color.White, fontSize = 12.sp))
            }
        }
    )
}


@Composable
private fun TodoListItem(
    item: Todo,
    onItemCheckedChanged: (Int, Boolean) -> Unit,
    index: Int,
) {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            colors = CheckboxDefaults.colors(Color.Blue),
            checked = item.isChecked,
            onCheckedChange = {
                onItemCheckedChanged(index, !item.isChecked)
            }
        )
        Text(
            text = item.text,
            modifier = Modifier.padding(start = 16.dp),
            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
            style = TextStyle(
                color = Color.Black,
                fontSize = 14.sp
            )
        )
    }
}


@Composable
private fun ContentWithProgress() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            contentAlignment = Alignment.TopCenter
        ) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
//            CircularProgressIndicator()
        }
    }
}