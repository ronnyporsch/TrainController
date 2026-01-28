package de.ronnyporsch.train_controller

class WasmPlatform : Platform {
    override val name = PlatformName.WasmJs
}

actual fun getPlatform(): Platform = WasmPlatform()