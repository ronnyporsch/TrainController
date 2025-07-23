package de.ronnyporsch.train_controller.util

interface Logger {
    /**
     * @param msg the message to log
     * @param severity the severity
     * @param tag the tag for the log
     */
    fun log(msg: String, tag: Tag = Tag.GENERAL_LOG, severity: Severity)

    /**
     * convenience function for error logs
     */
    fun e(msg: String, tag: Tag = Tag.GENERAL_LOG) {
        log(msg, tag, Severity.Error)
    }

    /**
     * convenience function for error logs that prints the throwable stack trace
     */
    fun e(throwable: Throwable, tag: Tag = Tag.GENERAL_LOG) {
        e(throwable.stackTraceToString(), tag)
    }

    /**
     * convenience function for warning logs
     */
    fun w(msg: String, tag: Tag = Tag.GENERAL_LOG) {
        log(msg, tag, Severity.Warning)
    }

    /**
     * convenience function for info logs
     */
    fun i(msg: String, tag: Tag = Tag.GENERAL_LOG) {
        log(msg, tag, Severity.Info)
    }

    /**
     * convenience function for debug logs
     */
    fun d(msg: String, tag: Tag = Tag.GENERAL_LOG) {
        log(msg, tag, Severity.Debug)
    }

}

val logger = object : Logger {
    override fun log(msg: String, tag: Tag, severity: Severity) {
        println("[${severity.abbreviation}/${tag.name}]: $msg")
    }
}

/**
 * Log severities
 */
enum class Severity(val abbreviation: Char) {
    Error('E'),
    Warning('W'),
    Info('I'),
    Debug('D')
}

/**
 * tags used for logging
 */
enum class Tag {
    GENERAL_LOG,
}