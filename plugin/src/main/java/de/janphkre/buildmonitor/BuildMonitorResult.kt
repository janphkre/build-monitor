package de.janphkre.buildmonitor

class BuildMonitorResult {

    val environment = HashMap<String, String>()

    override fun toString(): String {
        return StringBuilder()
            .append("{\"properties\":{")
            .apply { environment.entries.joinTo(this, separator = ",") { "\"${it.key}\":\"${it.value}\"" } }
            .append("}}")
            .toString()
    }
}