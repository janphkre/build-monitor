package de.janphkre.buildmonitor.actions.properties

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.result.BuildMonitorResult
import de.janphkre.buildmonitor.actions.IBuildMonitorAction
import org.gradle.api.Project

class EnvironmentMonitorAction: IBuildMonitorAction {

    private var result: HashMap<String, Any?>? = null

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
        result = HashMap<String, Any?>(systemProperties.size).apply {
            systemProperties.forEach {
                val value = properties[it] ?: return@forEach
                put(it, value)
            }
        }
    }

    override fun writeResultTo(buildMonitorResult: BuildMonitorResult) {
        buildMonitorResult.values["environment"] = result
    }
}