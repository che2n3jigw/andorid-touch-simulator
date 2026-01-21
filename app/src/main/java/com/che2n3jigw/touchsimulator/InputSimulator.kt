/*
 * Copyright (c) 2026 che2n3jigw.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

// 作者: che2n3jigw
// 邮箱: che2n3jigw@163.com
// 博客: che2n3jigw.github.io
// 创建时间： 1/21/26
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