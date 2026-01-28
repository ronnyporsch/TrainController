package de.ronnyporsch.train_controller

class JVMPlatform: Platform {
    override val name = PlatformName.Desktop
}

actual fun getPlatform(): Platform = JVMPlatform()