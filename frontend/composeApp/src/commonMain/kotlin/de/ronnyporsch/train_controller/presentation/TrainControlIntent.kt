package de.ronnyporsch.train_controller.presentation

sealed class TrainControlIntent {
    data class ChangeSpeed(val trainId: Long, val speed: Int) : TrainControlIntent()
}
