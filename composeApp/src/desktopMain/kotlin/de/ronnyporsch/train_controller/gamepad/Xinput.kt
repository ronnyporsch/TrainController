package de.ronnyporsch.train_controller.gamepad

import com.sun.jna.*
import com.sun.jna.win32.StdCallLibrary

/**
 * see https://en.wikipedia.org/wiki/DirectInput#XInput
 */
object XInput {
    @Suppress("FunctionName")
    interface XInputLibrary : StdCallLibrary {
        fun XInputGetState(dwUserIndex: Int, pState: XInputState): Int
        fun XInputSetState(dwUserIndex: Int, pVibration: XInputVibration): Int
    }

    val INSTANCE: XInputLibrary = try {
        Native.load("XInput1_4", XInputLibrary::class.java)
    } catch (_: UnsatisfiedLinkError) {
        try {
            Native.load("XInput9_1_0", XInputLibrary::class.java)
        } catch (_: UnsatisfiedLinkError) {
            Native.load("XInput1_3", XInputLibrary::class.java)
        }
    }

    class XInputGamepad : Structure() {
        @JvmField
        var wButtons: Short = 0
        @JvmField
        var bLeftTrigger: Byte = 0
        @JvmField
        var bRightTrigger: Byte = 0
        @JvmField
        var sThumbLX: Short = 0
        @JvmField
        var sThumbLY: Short = 0
        @JvmField
        var sThumbRX: Short = 0
        @JvmField
        var sThumbRY: Short = 0

        override fun getFieldOrder() = listOf(
            "wButtons", "bLeftTrigger", "bRightTrigger",
            "sThumbLX", "sThumbLY", "sThumbRX", "sThumbRY"
        )
    }

    class XInputState : Structure() {
        @JvmField
        var dwPacketNumber: Int = 0
        @JvmField
        var Gamepad: XInputGamepad = XInputGamepad()
        override fun getFieldOrder() = listOf("dwPacketNumber", "Gamepad")
    }

    class XInputVibration : Structure() {
        @JvmField
        var wLeftMotorSpeed: Short = 0
        @JvmField
        var wRightMotorSpeed: Short = 0
        override fun getFieldOrder() = listOf("wLeftMotorSpeed", "wRightMotorSpeed")
    }

    const val ERROR_SUCCESS = 0
}