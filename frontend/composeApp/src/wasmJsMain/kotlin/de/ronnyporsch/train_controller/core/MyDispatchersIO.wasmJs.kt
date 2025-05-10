package de.ronnyporsch.train_controller.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val Dispatchers.MyIO: CoroutineDispatcher
    get() = Default