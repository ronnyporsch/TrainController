package de.ronnyporsch.train_controller

class AndroidPlatform : Platform {
    override val name = PlatformName.Android
}

actual fun getPlatform(): Platform = AndroidPlatform()