# TouchSimulator

`TouchSimulator` 是一个用于模拟 Android 设备触摸事件的系统级应用。该项目的灵感来源于著名的 [scrcpy](https://github.com/Genymobile/scrcpy) 项目，并参考了其 `scrcpy-server` 部分的实现原理，但做出了关键性的简化。

由于 `TouchSimulator` 被设计为以系统权限运行，因此它无需像 scrcpy 那样为了获取 `Context` 或注入事件而将自身伪装成一个标准的 Android 应用。相反，它可以直接、高效地调用系统内部 API 来模拟触摸操作。

## 核心原理

Android 系统提供了一系列内部服务（Internal Services）用于管理输入事件，其中核心的是 `InputManager`。通过获取 `InputManager` 的实例，我们可以直接向系统注入（inject）各种输入事件，包括触摸、按键、鼠标等。

本项目利用了这一机制。由于应用以系统权限运行，它可以无障碍地访问到 `android.hardware.input.InputManager`，并调用其 `injectInputEvent` 方法。这使得我们能够精确地构造和发送 `MotionEvent` 对象，从而在屏幕的任意位置模拟按下（`ACTION_DOWN`）、移动（`ACTION_MOVE`）和抬起（`ACTION_UP`）等一系列触摸手势。

这种方法的优势在于：
*   **高权限**：直接与系统底层服务交互，模拟的事件与真实的用户触摸事件几乎没有区别。
*   **高效率**：绕过了应用层的事件分发和处理流程，减少了延迟。
*   **简洁性**：无需复杂的 `Context` 依赖或窗口（`Window`）创建，代码实现更为直接。

## 项目结构与用法

根据您提供的 `TouchSimulatorActivity.kt` 文件，项目的基本用法如下：

1.  **初始化**: 在应用启动时（例如 `Activity` 的 `onCreate` 方法中），获取一个控制器实例。
2.  **构造事件**: 创建一个 `Position` 对象来定义触摸的位置、屏幕尺寸等信息。
3.  **注入事件**: 调用控制器的 `injectTouch` 方法，并传入事件类型（如 `MotionEvent.ACTION_DOWN`）、触摸点ID、位置信息以及压力值。

### 代码示例
```kotlin
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
```


## 如何构建和部署

由于本项目需要系统权限才能正常工作，标准的 `adb install` 命令无法授予其必要的权限。您需要：

1.  **平台签名**：使用您所编译的 Android 系统 ROM 的平台密钥（platform keys）对生成的 APK 文件进行签名。
2.  **系统应用**：将签名后的 APK 推送到设备的 `/system/priv-app` 或 `/system/app` 目录下。
3.  **重启设备**：重启设备以使系统加载新的应用。

完成以上步骤后，`TouchSimulator` 应用将拥有足够的权限来调用内部 API 并成功模拟触摸事件。

## 未来展望

*   实现更复杂的手势模拟（如滑动、多点触控）。
*   通过网络或蓝牙接收指令，实现远程控制。
*   提供一个更完善的服务（Service）来后台运行触摸模拟任务。