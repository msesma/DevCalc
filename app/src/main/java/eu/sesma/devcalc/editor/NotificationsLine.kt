package eu.sesma.devcalc.editor


data class NotificationsLine(
    val error: String = "",
    val shifted: Boolean = false,
    val mode: String = "°",
)