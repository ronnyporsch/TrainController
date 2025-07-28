package de.ronnyporsch.train_controller.presentation

sealed class GamepadIntent() {
    data object SelectHoveredTrain : GamepadIntent()
    data object DeselectHoveredTrain : GamepadIntent()
    data object HoverNextTrain : GamepadIntent()
    data object HoverPreviousTrain : GamepadIntent()
    data object ToggleReverseDirection : GamepadIntent()
    data object IncreaseSpeed : GamepadIntent()
    data object DecreaseSpeed : GamepadIntent()
    data object Stop : GamepadIntent()
    data object ToggleLight : GamepadIntent()

}