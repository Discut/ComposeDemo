package com.example.composetodo

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.base.flowbus.FlowBus
import com.example.base.flowbus.MainScreenToastEvent

@SuppressLint("UnrememberedMutableState")
@Composable
internal fun AboutScreen() {

    val scope = LocalLifecycleOwner.current.lifecycleScope

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = {
            FlowBus.with<MainScreenToastEvent>().post(scope, MainScreenToastEvent("Hello world"))
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
