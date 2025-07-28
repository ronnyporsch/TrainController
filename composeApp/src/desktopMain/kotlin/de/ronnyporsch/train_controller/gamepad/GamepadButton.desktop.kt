package de.ronnyporsch.train_controller.gamepad

val GamepadButton.code: Int
    get() = when (this) {
        GamepadButton.DPadUp -> 0x0001
        GamepadButton.DPadDown -> 0x0002
        GamepadButton.DPadLeft -> 0x0004
        GamepadButton.DPadRight -> 0x0008
        GamepadButton.Start -> 0x0010
        GamepadButton.Back -> 0x0020
        GamepadButton.LeftThumb -> 0x0040
        GamepadButton.RightThumb -> 0x0080
        GamepadButton.LeftShoulder -> 0x0100
        GamepadButton.RightShoulder -> 0x0200
        GamepadButton.A -> 0x1000
        GamepadButton.B -> 0x2000
        GamepadButton.X -> 0x4000
        GamepadButton.Y -> 0x8000
    }