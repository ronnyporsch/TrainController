package de.ronnyporsch.train_controller

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform