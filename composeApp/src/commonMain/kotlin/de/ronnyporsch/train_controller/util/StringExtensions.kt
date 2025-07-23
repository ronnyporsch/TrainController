package de.ronnyporsch.train_controller.util

/**
 * example: "thisIsAnExampleString" -> This Is An Example String"
 */
fun String.camelCaseToCapitalizedWithSpaces(): String {
    return this.replace(Regex("([a-z])([A-Z])"), "$1 $2")
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
}