package de.ronnyporsch.train_controller.data

import de.ronnyporsch.train_controller.TRAIN_CONTROLLER_IP
import de.ronnyporsch.train_controller.TRAIN_CONTROLLER_PORT
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json

val restClient = initRestClient(TRAIN_CONTROLLER_IP, TRAIN_CONTROLLER_PORT)

fun initRestClient(serverIP: String, serverPort: Int) = HttpClient(CIO) {
    defaultRequest {
        host = serverIP
        port = serverPort
        url { protocol = URLProtocol.HTTP }
    }
    install(ContentNegotiation) {
        json()
    }
    install(HttpTimeout) {
        connectTimeoutMillis = 1000
    }
}