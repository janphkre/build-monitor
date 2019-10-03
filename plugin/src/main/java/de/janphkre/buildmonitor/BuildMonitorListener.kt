package de.janphkre.buildmonitor

import de.janphkre.buildmonitor.actions.IBuildMonitorAction
import de.janphkre.buildmonitor.actions.properties.EnvironmentMonitorAction
import de.janphkre.buildmonitor.actions.properties.GradlePropertiesMonitorAction
import de.janphkre.buildmonitor.report.IReporter
import de.janphkre.buildmonitor.util.EscapingJsonWriter
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import java.io.PrintWriter

class BuildMonitorListener(
    private val dslExtension: BuildMonitorExtension
): BuildListener {

    private var rootProject: Project? = null
    private val preMonitorActions: List<IBuildMonitorAction> = listOf()
    private val postMonitorActions: List<IBuildMonitorAction> = listOf(
        GradlePropertiesMonitorAction(),
        EnvironmentMonitorAction()
    )

    override fun buildFinished(buildResult: BuildResult) {
        when(buildResult.action) {
            "Build" -> {
                val monitorResult = collectResult()
                monitorBuildResult(monitorResult, buildResult)
                reportMonitorResult(monitorResult)
                rootProject = null
            }
            else -> { //Configure
                println("BuildMonitorListener: ConfigureFinished")
                //TODO("Not invoked at the moment")
            }
        }
    }

    private fun collectResult(): BuildMonitorResult {
        val intermediateResult = preMonitorActions.map { it.getResult() }
            .fold(BuildMonitorResult()) { monitorResult, partialResult ->
                partialResult?.writeTo(monitorResult) ?: monitorResult
            }
        return postMonitorActions.map { it.monitor(rootProject!!, dslExtension); it.getResult() }
            .fold(intermediateResult) { monitorResult, partialResult ->
                partialResult?.writeTo(monitorResult) ?: monitorResult
            }
    }

    private fun monitorBuildResult(monitorResult: BuildMonitorResult, buildResult: BuildResult) {
        val failure = buildResult.failure
        if(failure != null) {
            val stringWriter = EscapingJsonWriter()
            failure.printStackTrace(PrintWriter(stringWriter))
            monitorResult.result["exception"] = stringWriter.toString()
            monitorResult.result["status"] = BuildResultType.FAILURE.name
        } else {
            monitorResult.result["status"] = BuildResultType.SUCCESS.name
        }
    }

    private fun reportMonitorResult(monitorResult: BuildMonitorResult) {
        val reporter = IReporter.reportFor(dslExtension, rootProject!!.buildDir)
        reporter.report(monitorResult)
    }

    override fun projectsEvaluated(gradle: Gradle) {
        rootProject = gradle.rootProject
        preMonitorActions.forEach {
            it.monitor(rootProject!!, dslExtension)
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
}