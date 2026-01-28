package de.ronnyporsch.train_controller

interface Platform {
    val name: PlatformName
}

@Suppress("unused")
enum class PlatformName {
    Android, IOS, Desktop, WasmJs
}

expect fun getPlatform(): Platform