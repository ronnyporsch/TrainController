package de.ronnyporsch.train_controller.gamepad

val GamepadButton.code: Int
    get() = when (this) {
        GamepadButton.DPadUp -> 19
        GamepadButton.DPadDown -> 20
        GamepadButton.DPadLeft -> 21
        GamepadButton.DPadRight -> 22
        GamepadButton.Start -> 108
        GamepadButton.Back -> 109
        GamepadButton.LeftThumb -> 106
        GamepadButton.RightThumb -> 107
        GamepadButton.LeftShoulder -> 102
        GamepadButton.RightShoulder -> 103
        GamepadButton.A -> 96
        GamepadButton.B -> 97
        GamepadButton.X -> 99
        GamepadButton.Y -> 100
    }