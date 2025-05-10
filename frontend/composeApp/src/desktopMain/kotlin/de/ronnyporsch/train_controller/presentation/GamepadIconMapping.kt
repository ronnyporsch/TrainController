package de.ronnyporsch.train_controller.presentation

import de.ronnyporsch.train_controller.gamepad.GamepadActuator
import de.ronnyporsch.train_controller.gamepad.GamepadButton
import de.ronnyporsch.train_controller.gamepad.GamepadTrigger
import kotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource

val gamepadIconMapping: Map<GamepadActuator, DrawableResource> = mapOf(
    GamepadButton.A to Res.drawable.xbox_button_color_a,
    GamepadButton.B to Res.drawable.xbox_button_color_b,
    GamepadButton.X to Res.drawable.xbox_button_color_x,
    GamepadButton.Y to Res.drawable.xbox_button_color_y,
    GamepadButton.LeftShoulder to Res.drawable.xbox_lb,
    GamepadButton.RightShoulder to Res.drawable.xbox_rb,
    GamepadButton.DPadLeft to Res.drawable.xbox_dpad_left,
    GamepadButton.DPadRight to Res.drawable.xbox_dpad_right,
    GamepadButton.Back to Res.drawable.xbox_button_view,
    GamepadTrigger.LeftTrigger to Res.drawable.xbox_lt,
    GamepadTrigger.RightTrigger to Res.drawable.xbox_rt
)