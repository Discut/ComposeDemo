package com.example.composetodo

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.base.flowbus.FlowBus
import com.example.base.flowbus.MainScreenToastEvent

@SuppressLint("UnrememberedMutableState")
@Composable
internal fun AboutScreen() {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val rememberCoroutineScope = rememberCoroutineScope()
        Button(onClick = {
            FlowBus.with<MainScreenToastEvent>()
                .post(rememberCoroutineScope, MainScreenToastEvent("Hello world"))
        }) {
            Text(text = "通过FlowBus发送消息")
        }
    }
}


@Preview
@Composable
fun Review() {
    AboutScreen()
}
