package de.ronnyporsch.train_controller

import java.io.File

lateinit var backendProcess : Process
fun startBackend() {
    println("Starting backend")
    val appDir = File(System.getProperty("user.dir"))
    val backendPath = File(appDir, "bin/TrainController.exe").absolutePath
    backendProcess = ProcessBuilder(backendPath)
        .inheritIO()
        .start()
}

fun stopBackend() {
    println("Stopping backend")
    backendProcess.destroyForcibly()
}
