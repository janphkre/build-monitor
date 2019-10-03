package de.janphkre.buildmonitor

import de.janphkre.buildmonitor.properties.EnvironmentMonitorAction
import de.janphkre.buildmonitor.properties.GradlePropertiesMonitorAction
import de.janphkre.buildmonitor.report.IReporter
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle

class BuildMonitorListener(
    private val dslExtension: BuildMonitorExtension
): BuildListener {

    private val monitorActions: List<IBuildMonitorAction> = listOf(
        GradlePropertiesMonitorAction(),
        EnvironmentMonitorAction()
    )

    override fun buildFinished(result: BuildResult) {
        when(result.action) {
            "Build" -> {
                println("BuildFinished: $result")
                //TODO()
            }
            else -> { //Configure
                println("ConfigureFinished: $result")
                //TODO("Not invoked at the moment")
            }
        }
    }

    override fun projectsEvaluated(gradle: Gradle) {
        val monitorResult = monitorActions.map { it.monitor(gradle.rootProject, dslExtension) }
            .fold(BuildMonitorResult()) { result, partialResult ->
                partialResult.writeTo(result)
            }
        val reporter = IReporter.reportFor(dslExtension, gradle.rootProject.buildDir)
        reporter.report(monitorResult)

    }

    override fun settingsEvaluated(settings: Settings) { }
    override fun projectsLoaded(gradle: Gradle) { }
    override fun buildStarted(gradle: Gradle) { }
}