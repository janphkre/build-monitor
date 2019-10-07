package de.janphkre.buildmonitor.actions.properties

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.actions.IBuildMonitorAction
import de.janphkre.buildmonitor.result.BuildMonitorResult
import org.gradle.api.Project
import java.lang.management.ManagementFactory

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
        result = HashMap<String, Any?>(systemProperties.size + 2).apply {
            systemProperties.forEach {
                val value = properties[it] ?: return@forEach
                put(it, value)
            }
            put("processors", Runtime.getRuntime().availableProcessors())
            put("gcTime", ManagementFactory.getGarbageCollectorMXBeans().fold(0L) { sum, it -> sum + it.collectionTime })//TODO: Should be monitored when the build has completed

//            put("jvmArguments", ManagementFactory.getRuntimeMXBean().inputArguments)//TODO: DOES NOT CONTAIN JAVA XMX ARGUMENTS or org.gradle.jvmargs=-Xmx1536m
        }
    }

    override fun writeResultTo(buildMonitorResult: BuildMonitorResult) {
        buildMonitorResult.values["environment"] = result
    }
}