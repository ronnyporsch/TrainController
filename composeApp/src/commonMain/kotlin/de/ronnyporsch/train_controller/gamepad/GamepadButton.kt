package de.ronnyporsch.train_controller.gamepad

enum class GamepadButton : GamepadActuator {
    DPadUp,
    DPadDown,
    DPadLeft,
    DPadRight,
    Start,
    Back,
    LeftThumb,
    RightThumb,
    LeftShoulder,
    RightShoulder,
    A,
    B,
    X,
    Y
}

enum class GamepadTrigger: GamepadActuator {
    LeftTrigger, RightTrigger;
}

//marker interface
sealed interface GamepadActuator