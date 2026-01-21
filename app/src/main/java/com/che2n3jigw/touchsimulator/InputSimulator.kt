package com.che2n3jigw.touchsimulator

import android.content.Context
import android.content.Context.INPUT_SERVICE
import android.hardware.input.InputManager
import android.util.Log
import android.view.InputEvent
import android.view.MotionEvent
import java.lang.reflect.Method

class InputSimulator(private val context: Context) {

    companion object {
        private const val TAG = "InputSimulator"
    }

    private val inputManager: InputManager by lazy {
        context.getSystemService(INPUT_SERVICE) as InputManager
    }

    private val injectInputEventMethod: Method by lazy {
        // 反射获取隐藏 API: injectInputEvent
        InputManager::class.java.getMethod(
            "injectInputEvent",
            InputEvent::class.java,
            Int::class.java
        )
    }

    fun injectEvent(event: MotionEvent): Boolean {
        try {
            // mode 说明: 0 = ASYNC (异步), 1 = SYNC (同步并等待), 2 = WAIT_FOR_RESULT
            // 系统应用通常使用 0 或 2
            injectInputEventMethod.invoke(inputManager, event, 0)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "injectEvent: ", e)
            return false
        }
    }
}