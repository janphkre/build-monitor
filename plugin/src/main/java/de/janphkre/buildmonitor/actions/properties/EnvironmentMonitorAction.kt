package de.janphkre.buildmonitor.actions.properties

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.result.BuildMonitorResult
import de.janphkre.buildmonitor.actions.IBuildMonitorAction
import org.gradle.api.Project

class EnvironmentMonitorAction: IBuildMonitorAction {

    private var result: List<Pair<String, String>>? = null

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
        result = systemProperties.mapNotNull {
            val value = properties[it] ?: return@mapNotNull null
            Pair(it, value.toString())
        }
    }

    override fun writeResultTo(buildMonitorResult: BuildMonitorResult) {
        result?.let { buildMonitorResult.environment.putAll(it) }
    }
}