package de.janphkre.buildmonitor

import de.janphkre.buildmonitor.properties.EnvironmentMonitorAction
import de.janphkre.buildmonitor.properties.GradlePropertiesMonitorAction
import de.janphkre.buildmonitor.report.IReporter
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
        val dslExtension = target.extensions.create("buildMonitor", BuildMonitorExtension::class.java)
//TODO: We get this extension, but it is empty since the configuration of this project has not completed yet.

        val monitorResult = monitorActions.map { it.monitor(target, dslExtension) }.fold(BuildMonitorResult()) { result, partialResult ->
            partialResult.writeTo(result)
        }

        val reporter = IReporter.reportFor(dslExtension, target.buildDir)
        reporter.report(monitorResult)
    }
}