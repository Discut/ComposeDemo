package com.example.base.flowbus

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

object FlowBus {
    private var eventsMap = ConcurrentHashMap<Any, FlowBus<*, *>>()
    private var stickEventsMap = ConcurrentHashMap<Any, FlowBus<*, *>>()

    /**
     * 获取事件流对象
     */
    fun <T : Any, E : Any> with(key: T): FlowBus<T, E> {
        eventsMap[key]?.apply {
            return this as FlowBus<T, E>
        }
        eventsMap[key] = FlowBus<T, E>(key)
        return eventsMap[key] as FlowBus<T, E>
    }

    /**
     * 获取粘连事件流对象
     */
    fun <T : Any, E : Any> withStick(key: T): StickFlowBus<T, E> {
        stickEventsMap[key]?.apply {
            return this as StickFlowBus<T, E>
        }
        stickEventsMap[key] = StickFlowBus<T, E>(key)
        return stickEventsMap[key] as StickFlowBus<T, E>
    }


    open class FlowBus<T : Any, E>(private val key: T) : DefaultLifecycleObserver {

        private val _events: MutableSharedFlow<E> by lazy {
            obtainFlow()
        }

        val events = _events.asSharedFlow()
        internal open fun obtainFlow(): MutableSharedFlow<E> =
            MutableSharedFlow(0, 1, BufferOverflow.DROP_OLDEST)

        fun register(lifecycleOwner: LifecycleOwner, action: (value: E) -> Unit) {
            lifecycleOwner.lifecycle.addObserver(this)
            lifecycleOwner.lifecycleScope.launch {
                events.collect {
                    try {
                        action(it)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            // 注册生命周期监听器，用于销毁数据防止内存泄漏
            lifecycleOwner.lifecycle.addObserver(this)
        }

        suspend fun post(event: E) {
            _events.emit(event)
        }

        fun post(scope: CoroutineScope, event: E) {
            scope.launch {
                post(event)
            }
        }

        /**
         * 自动销毁
         *
         * @param owner 注册时生命周期
         */
        override fun onDestroy(owner: LifecycleOwner) {
            val count = _events.subscriptionCount.value
            if (count <= 0) {
                eventsMap.remove(key)
            }
        }
    }

    class StickFlowBus<T : Any, E>(private val key: T) : FlowBus<T, E>(key) {
        override fun obtainFlow(): MutableSharedFlow<E> =
            MutableSharedFlow(1, 1, BufferOverflow.DROP_OLDEST)
    }

}