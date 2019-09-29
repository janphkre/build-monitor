package de.janphkre.buildmonitor

import de.janphkre.buildmonitor.properties.EnvironmentMonitorAction
import de.janphkre.buildmonitor.properties.GradlePropertiesMonitorAction
import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildMonitorPlugin: Plugin<Project> {

    private val supportedGradleVersion = "5.4"
    private val monitorActions: List<IBuildMonitorAction> = listOf(
        GradlePropertiesMonitorAction(),
        EnvironmentMonitorAction()
    )

    override fun apply(target: Project) {
        if(!target.gradle.gradleVersion.startsWith(supportedGradleVersion)) {
            throw UnsupportedOperationException("Only gradle version $supportedGradleVersion.* is supported by this plugin version.")
        }

        val monitorResult = monitorActions.map { it.monitor(target) }.fold(BuildMonitorResult()) { result, partialResult ->
            partialResult.writeTo(result)
        }

        println(monitorResult)
        //TODO("REPORT RESULT THROUGH API")
    }
}