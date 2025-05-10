package de.ronnyporsch.train_controller.presentation

import de.ronnyporsch.train_controller.gamepad.GamepadButton

val gamepadButtonMapping = mapOf(
    GamepadButton.A to GamepadIntent.SelectHoveredTrain,
    GamepadButton.B to GamepadIntent.Stop,
    GamepadButton.X to GamepadIntent.ToggleLight,
    GamepadButton.Y to GamepadIntent.DeselectHoveredTrain,
    GamepadButton.LeftShoulder to GamepadIntent.DecreaseSpeed,
    GamepadButton.RightShoulder to GamepadIntent.IncreaseSpeed,
    GamepadButton.DPadLeft to GamepadIntent.HoverPreviousTrain,
    GamepadButton.DPadRight to GamepadIntent.HoverNextTrain,
    GamepadButton.Back to GamepadIntent.ToggleReverseDirection,
)