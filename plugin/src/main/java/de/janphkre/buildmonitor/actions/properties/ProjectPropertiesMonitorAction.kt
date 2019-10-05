package de.janphkre.buildmonitor.actions.properties

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.result.BuildMonitorResult
import de.janphkre.buildmonitor.actions.IBuildMonitorAction
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle

class ProjectPropertiesMonitorAction: IBuildMonitorAction {

    private var result: HashMap<String, Any?>? = null

    //Entries are used in monitor as PropertyHandlers.values()
    @Suppress("unused")
    private enum class GradlePropertyHandlers(val key: String, val handler: (Any) -> String) {
        PROJECT_DIR("projectDir", { it.toString() }),
        NAME("name", { it.toString() }),
        STATUS("status", { it.toString() }),
        GRADLE("gradle", { (it as Gradle).gradleVersion })
//TODO:        TASKS("tasks", { it.toString() }),
    }

    override fun monitor(target: Project, dslExtension: BuildMonitorExtension) {
        val properties = target.properties
        result = HashMap<String, Any?>(GradlePropertyHandlers.values().size).apply {
            GradlePropertyHandlers.values().forEach {
                val value = properties[it.key] ?: return@forEach
                put(it.key, it.handler.invoke(value))
            }
        }
    }

    override fun writeResultTo(buildMonitorResult: BuildMonitorResult) {
        buildMonitorResult.values["project"] = result
    }
}