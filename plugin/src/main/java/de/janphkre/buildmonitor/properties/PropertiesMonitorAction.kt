package de.janphkre.buildmonitor.properties

import de.janphkre.buildmonitor.IBuildMonitorAction
import de.janphkre.buildmonitor.IBuildMonitorActionResult
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle

class PropertiesMonitorAction: IBuildMonitorAction {

    //Entries are used in monitor as PropertyHandlers.values()
    @Suppress("unused")
    private enum class GradlePropertyHandlers(val key: String, val handler: (Any) -> String) {
        PROJECT_DIR("projectDir", { it.toString() }),
        VERSION("version", { it.toString() }),
        NAME("name", { it.toString() }),
        STATUS("status", { it.toString() }),
        GRADLE("gradle", { (it as Gradle).gradleVersion })
//        TASKS("tasks", { it.toString() }),
//        DEPENDENCIES("dependencies", { it.toString() })
    }

    override fun monitor(target: Project): IBuildMonitorActionResult {
        val properties = target.properties
        return PropertiesMonitorActionResult(GradlePropertyHandlers.values().mapNotNull {
            val value = properties[it.key] ?: return@mapNotNull null
            Pair(it.key, it.handler.invoke(value))

        })
    }
}