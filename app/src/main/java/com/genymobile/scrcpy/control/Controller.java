package com.genymobile.scrcpy.control;

import android.content.Context;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;

import com.che2n3jigw.touchsimulator.InputSimulator;
import com.genymobile.scrcpy.device.Point;
import com.genymobile.scrcpy.device.Position;
import com.genymobile.scrcpy.util.Ln;


public class Controller {
    private static final int DEFAULT_DEVICE_ID = 0;

    private long lastTouchDown;

    private final PointersState pointersState = new PointersState();
    private final MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[PointersState.MAX_POINTERS];
    private final MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[PointersState.MAX_POINTERS];

    private final InputSimulator inputSimulator;

    public Controller(Context context) {
        inputSimulator = new InputSimulator(context);
        initPointers();
    }

    private void initPointers() {
        for (int i = 0; i < PointersState.MAX_POINTERS; ++i) {
            MotionEvent.PointerProperties props = new MotionEvent.PointerProperties();
            props.toolType = MotionEvent.TOOL_TYPE_FINGER;

            MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
            coords.orientation = 0;
            coords.size = 0;

            pointerProperties[i] = props;
            pointerCoords[i] = coords;
        }
    }

    /**
     * 向设备注入触摸事件。
     *
     * @param action    触摸动作，例如 {@link MotionEvent#ACTION_DOWN}。
     * @param pointerId 指针的唯一标识符。
     * @param position  事件发生的位置。
     * @param pressure  事件的压力。
     * @return 如果事件成功注入，则返回 {@code true}。
     */
    public boolean injectTouch(int action, long pointerId, Position position, float pressure) {
        // 获取当前系统时间
        long now = SystemClock.uptimeMillis();
        // 将屏幕位置转换为显示器上的具体坐标点
        Point point = position.getPoint();

        // 获取或分配一个指针索引
        int pointerIndex = pointersState.getPointerIndex(pointerId);
        if (pointerIndex == -1) {
            // 如果指针数量超过最大限制，则记录警告并返回
            Ln.w("Too many pointers for touch event");
            return false;
        }
        // 更新指针的状态
        Pointer pointer = pointersState.get(pointerIndex);
        pointer.setPoint(point);
        pointer.setPressure(pressure);

        // 将工具类型设置为手指，以模拟触摸事件
        // POINTER_ID_GENERIC_FINGER, POINTER_ID_VIRTUAL_FINGER or real touch from device
        pointerProperties[pointerIndex].toolType = MotionEvent.TOOL_TYPE_FINGER;
        // 将事件源设置为触摸屏
        int source = InputDevice.SOURCE_TOUCHSCREEN;
        // 标记指针是否已抬起
        pointer.setUp(action == MotionEvent.ACTION_UP);

        // 更新所有指针的属性和坐标
        int pointerCount = pointersState.update(pointerProperties, pointerCoords);
        if (pointerCount == 1) {
            // 如果是单点触摸
            if (action == MotionEvent.ACTION_DOWN) {
                // 记录首次按下的时间
                lastTouchDown = now;
            }
        } else {
            // 如果是多点触摸，需要使用 ACTION_POINTER_* 动作
            // secondary pointers must use ACTION_POINTER_* ORed with the pointerIndex
            if (action == MotionEvent.ACTION_UP) {
                // 次要指针抬起
                action = MotionEvent.ACTION_POINTER_UP | (pointerIndex << MotionEvent.ACTION_POINTER_INDEX_SHIFT);
            } else if (action == MotionEvent.ACTION_DOWN) {
                // 次要指针按下
                action = MotionEvent.ACTION_POINTER_DOWN | (pointerIndex << MotionEvent.ACTION_POINTER_INDEX_SHIFT);
            }
        }

        // 创建 MotionEvent 对象
        MotionEvent event = MotionEvent.obtain(lastTouchDown, now, action, pointerCount, pointerProperties, pointerCoords, 0, 0, 1f, 1f,
                DEFAULT_DEVICE_ID, 0, source, 0);
        // 异步注入事件到设备
        return inputSimulator.injectEvent(event);
    }
}
