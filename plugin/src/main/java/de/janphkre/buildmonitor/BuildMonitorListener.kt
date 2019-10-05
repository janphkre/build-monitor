package de.janphkre.buildmonitor

import de.janphkre.buildmonitor.actions.IBuildMonitorAction
import de.janphkre.buildmonitor.actions.configuration.ConfigurationMonitorAction
import de.janphkre.buildmonitor.actions.properties.EnvironmentMonitorAction
import de.janphkre.buildmonitor.actions.properties.GradlePropertiesMonitorAction
import de.janphkre.buildmonitor.actions.properties.ProjectPropertiesMonitorAction
import de.janphkre.buildmonitor.reporting.IReporter
import de.janphkre.buildmonitor.result.BuildMonitorResult
import de.janphkre.buildmonitor.util.EscapingJsonWriter
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import java.io.File

class BuildMonitorListener(
    private val dslExtension: BuildMonitorExtension
): BuildListener {

    private val monitorResult = BuildMonitorResult()
    private val preMonitorActions: List<IBuildMonitorAction> = listOf()
    private val postMonitorActions: List<IBuildMonitorAction> = listOf(
        ProjectPropertiesMonitorAction(),
        EnvironmentMonitorAction(),
        GradlePropertiesMonitorAction(),
        ConfigurationMonitorAction()
    )

    override fun buildFinished(buildResult: BuildResult) {
        when(buildResult.action) {
            "Build" -> {
                collectResult(buildResult.gradle)
                monitorBuildResult(buildResult)
                reportMonitorResult(buildResult.gradle)
                //TODO: CAN WE REPORT THE RESULT LATER?
            }
            else -> { //Configure
                println("BuildMonitorListener: ConfigureFinished")
                //TODO("Not invoked at the moment")
            }
        }
    }

    private fun collectResult(gradle: Gradle?) {
        preMonitorActions.fold(monitorResult) { monitorResult, action ->
            action.writeResultTo(monitorResult)
            monitorResult
        }
        //TODO: Figure out a way to always have a gradle instance when the build fails?!
        gradle?.rootProject?.let { rootProject ->
            postMonitorActions.fold(monitorResult) { monitorResult, action ->
                action.monitor(rootProject, dslExtension)
                action.writeResultTo(monitorResult)
                monitorResult
            }
        }
    }

    private fun monitorBuildResult(buildResult: BuildResult) {
        val failure = buildResult.failure
        val result = HashMap<String, Any?>()
        if(failure != null) {
            result[RESULT_EXCEPTION_KEY] = EscapingJsonWriter.writeFailure(failure)
            result[RESULT_STATUS_KEY] = BuildResultType.FAILURE.name
        } else {
            result[RESULT_STATUS_KEY] = BuildResultType.SUCCESS.name
        }
        monitorResult.values["result"] = result
    }

    private fun reportMonitorResult(gradle: Gradle?) {
        val reporter = IReporter.reportFor(dslExtension, gradle?.rootProject?.buildDir ?: File("build/"))
        reporter.report(monitorResult)
    }

    override fun projectsEvaluated(gradle: Gradle) {
        preMonitorActions.forEach {
            it.monitor(gradle.rootProject, dslExtension)
        }
    }

    override fun settingsEvaluated(settings: Settings) {
        println("BuildMonitorListener: SettingsEvaluated")
    }

    override fun projectsLoaded(gradle: Gradle) {
        println("BuildMonitorListener: ProjectsLoaded")
    }

    override fun buildStarted(gradle: Gradle) {
        println("BuildMonitorListener: BuildStarted")
    }

    enum class BuildResultType {
        SUCCESS, FAILURE
    }

    companion object {
        private const val RESULT_STATUS_KEY = "status"
        private const val RESULT_EXCEPTION_KEY = "exception"
    }
}