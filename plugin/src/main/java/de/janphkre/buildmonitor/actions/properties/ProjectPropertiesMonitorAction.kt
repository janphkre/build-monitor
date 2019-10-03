package de.janphkre.buildmonitor.actions.properties

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.result.BuildMonitorResult
import de.janphkre.buildmonitor.actions.IBuildMonitorAction
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle

class ProjectPropertiesMonitorAction: IBuildMonitorAction {

    private var result: List<Pair<String, String>>? = null

    //Entries are used in monitor as PropertyHandlers.values()
    @Suppress("unused")
    private enum class GradlePropertyHandlers(val key: String, val handler: (Any) -> String) {
        PROJECT_DIR("projectDir", { it.toString() }),
        NAME("name", { it.toString() }),
        STATUS("status", { it.toString() }),
        GRADLE("gradle", { (it as Gradle).gradleVersion })
//        TASKS("tasks", { it.toString() }),
//        DEPENDENCIES("dependencies", { it.toString() })
    }

    override fun monitor(target: Project, dslExtension: BuildMonitorExtension) {
        val properties = target.properties
        result = GradlePropertyHandlers.values().mapNotNull {
            val value = properties[it.key] ?: return@mapNotNull null
            Pair("project.${it.key}", it.handler.invoke(value))
        }
    }

    override fun writeResultTo(buildMonitorResult: BuildMonitorResult) {
        result?.let { buildMonitorResult.environment.putAll(it) }
    }
}