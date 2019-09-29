package de.janphkre.buildmonitor.properties

import de.janphkre.buildmonitor.IBuildMonitorAction
import de.janphkre.buildmonitor.IBuildMonitorActionResult
import org.gradle.api.Project

class EnvironmentMonitorAction: IBuildMonitorAction {

    private val systemProperties = listOf(
        "java.version",
        "java.vendor",
        "os.name",
        "os.arch",
        "os.version",
        "user.name"
    )

    override fun monitor(target: Project): IBuildMonitorActionResult {
        val properties = System.getProperties()
        return PropertiesMonitorActionResult(systemProperties.mapNotNull {
            val value = properties[it] ?: return@mapNotNull null
            Pair(it, value.toString())

        })
    }
}