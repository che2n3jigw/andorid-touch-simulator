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