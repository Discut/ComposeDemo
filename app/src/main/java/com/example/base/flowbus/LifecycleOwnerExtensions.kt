package com.example.base.flowbus

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * 扩展LifecycleOwner，添加快速监听事件的方法
 *
 * @param key 事件名称
 * @param dispatcher 调度程序
 * @param minActiveState 监听器启动的时机
 * @param action 消费获取的事件的方法
 */
inline fun <reified E : BusEvent> LifecycleOwner.observeEven(
    key:String = E::class.java.name,
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: (event: E) -> Unit
) {
    FlowBus.with<E>(key).register(this, dispatcher, minActiveState) {
        action(it)
    }
}