package de.janphkre.buildmonitor.actions.tasks

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.actions.IBuildMonitorAction
import de.janphkre.buildmonitor.result.BuildMonitorResult
import org.gradle.api.Project

class TaskMonitorAction: IBuildMonitorAction {

    private val tasks = HashMap<String, Map<String,Any>>()

    override fun monitor(target: Project, dslExtension: BuildMonitorExtension) {
        try {
            target.gradle.taskGraph.allTasks.forEach { task ->
                tasks[task.path] = HashMap<String, Any>().apply {
                    put("enabled", task.enabled)
                    task.state.let { state ->
                        put("executed", state.executed)
                        put("skipped", state.skipped)
                        put("noSource", state.noSource)
                        put("upToDate", state.upToDate)
                        put("didWork", state.didWork)
                    }
                    task.group?.let { put("group", it) }
                    task.inputs.let { inputs ->
                        if (inputs.hasInputs) {
                            put("inputFiles", inputs.files.files.map { it.path })
                        }
                        if (inputs.hasSourceFiles) {
                            put("inputSources", inputs.sourceFiles.files.map { it.path })
                        }
                        put("inputParameters", inputs.properties.mapValues { it.value?.toString() })
                    }
                    put("dependencies", task.dependsOn.map { it.toString() })
                }
                //TODO: How can we see why didWork was true? -> Which file / inputParameter / dependency caused that?

                // TODO: Task execution times
                // TODO: Task log output
            }
        } catch (ignored: IllegalStateException) {
            /* We can ignore this exception as it means that the task graph was not populated */
        }
    }

    override fun writeResultTo(buildMonitorResult: BuildMonitorResult) {
        buildMonitorResult.values["tasks"] = tasks
    }
}