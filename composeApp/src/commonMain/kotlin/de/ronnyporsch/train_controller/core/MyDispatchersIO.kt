package de.ronnyporsch.train_controller.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * on Android and JVM, Dispatchers.IO will be used
 * on wasmJs Dispatchers.IO is not available and has to be replaced by Dispatchers.Default
 */
expect val Dispatchers.MyIO : CoroutineDispatcher