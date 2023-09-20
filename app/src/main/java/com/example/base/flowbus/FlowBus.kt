package com.example.base.flowbus

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

object FlowBus {
    var eventsMap = ConcurrentHashMap<Any, FlowBus<out BusEvent>>()
    var stickEventsMap = ConcurrentHashMap<Any, FlowBus<out BusEvent>>()

    /**
     * 获取事件流对象
     */
    inline fun <reified E : BusEvent> with(key: String = E::class.java.name): FlowBus<E> {
        eventsMap[key]?.apply {
            return this as FlowBus<E>
        }
        eventsMap[key] = FlowBus<E>(key)
        return eventsMap[key] as FlowBus<E>
    }

    /**
     * 获取粘连事件流对象
     */
    inline fun <reified E : BusEvent> withStick(key: String = E::class.java.name): StickFlowBus<E> {
        stickEventsMap[key]?.apply {
            return this as StickFlowBus<E>
        }
        stickEventsMap[key] = StickFlowBus<E>(key)
        return stickEventsMap[key] as StickFlowBus<E>
    }


    open class FlowBus<E>(private val key: String) : DefaultLifecycleObserver {

        private val _events: MutableSharedFlow<E> by lazy {
            obtainFlow()
        }

        val events = _events.asSharedFlow()

        private lateinit var destroyObserver: () -> Unit
        internal open fun obtainFlow(): MutableSharedFlow<E> =
            MutableSharedFlow(0, 1, BufferOverflow.DROP_OLDEST)

        fun register(
            lifecycleOwner: LifecycleOwner,
            dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
            minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
            action: (value: E) -> Unit
        ) {
            // 观察当前上下文组件的生命周期
            lifecycleOwner.lifecycle.addObserver(this)
            // 构建注销观察器的函数
            destroyObserver = { lifecycleOwner.lifecycle.removeObserver(this) }
            // 启动一个协程用于消费事件
            lifecycleOwner.lifecycleScope.launch(dispatcher) {
                lifecycleOwner.repeatOnLifecycle(minActiveState) {
                    // 消费事件
                    events.collect {
                        try {
                            action(it)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
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
                stickEventsMap.remove(key)
            }
            destroyObserver()
        }
    }

    class StickFlowBus<E : BusEvent>(key: String) : FlowBus<E>(key) {
        override fun obtainFlow(): MutableSharedFlow<E> =
            MutableSharedFlow(1, 1, BufferOverflow.DROP_OLDEST)
    }

}