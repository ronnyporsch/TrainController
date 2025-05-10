package de.ronnyporsch.train_controller.domain

import androidx.compose.ui.graphics.Color

enum class PlayerColor(val trainColorCode: Int, val composeColor: Color) {
    BLACK(0, Color.Black),
    PINK(1, Color(0xFFFF00FF)),
    PURPLE(2, Color(0xFF800080)),
    BLUE(3, Color(0xFF0066ff)),
    LIGHT_BLUE(4, Color(0xFFADD8E6)),
    CYAN(5, Color.Cyan),
    GREEN(6, Color(0xFF009933)),
    YELLOW(7, Color.Yellow),
    ORANGE(8, Color(0xFFff9900)),
    RED(9, Color.Red),
    WHITE(10, Color.White),
    NONE(255, Color.Transparent);
}