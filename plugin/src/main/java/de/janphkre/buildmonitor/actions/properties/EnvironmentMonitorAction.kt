package de.janphkre.buildmonitor.actions.properties

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.actions.IBuildMonitorAction
import de.janphkre.buildmonitor.actions.IBuildMonitorActionResult
import org.gradle.api.Project

class EnvironmentMonitorAction: IBuildMonitorAction {

    private var result: IBuildMonitorActionResult? = null

    private val systemProperties = listOf(
        "java.version",
        "java.vendor",
        "os.name",
        "os.arch",
        "os.version",
        "user.name"
    )

    override fun monitor(target: Project, dslExtension: BuildMonitorExtension) {
        val properties = System.getProperties()
        result = PropertiesMonitorActionResult(systemProperties.mapNotNull {
            val value = properties[it] ?: return@mapNotNull null
            Pair(it, value.toString())

        })
    }

    override fun getResult(): IBuildMonitorActionResult? {
        return result
    }
}