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

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import com.genymobile.scrcpy.control.Controller
import com.genymobile.scrcpy.device.Position


class TouchSimulatorActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_touch_simulator)

        val controller = Controller(this)
        findViewById<Button>(R.id.btn_test).setOnClickListener {
            Thread {
                Thread.sleep(1000)
                val position = Position(500, 200, 1000, 1000)
                runOnUiThread {
                    controller.injectTouch(MotionEvent.ACTION_DOWN, 0, position, 1f)
                }
            }.start()
        }
    }
}