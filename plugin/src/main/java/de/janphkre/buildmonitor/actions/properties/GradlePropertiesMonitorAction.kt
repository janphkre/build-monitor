package de.janphkre.buildmonitor.actions.properties

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.actions.IBuildMonitorAction
import de.janphkre.buildmonitor.result.BuildMonitorResult
import org.gradle.api.Project

class GradlePropertiesMonitorAction: IBuildMonitorAction {

    private var result: HashMap<String, Any?>? = null

    override fun monitor(target: Project, dslExtension: BuildMonitorExtension) {
        target.gradle.startParameter.let { startParameter ->
            result = HashMap<String, Any?>(5).apply {
                put("excludedTasks", startParameter.excludedTaskNames)
                put("taskNames", startParameter.taskNames)
                put("initScripts", startParameter.allInitScripts.map { it.absolutePath })
                put("switches", HashMap<String, Any?>(16).apply {
                    put("buildCache", startParameter.isBuildCacheEnabled)
                    put("buildDependencies", startParameter.isBuildProjectDependencies)
                    put("configureOnDemand", startParameter.isConfigureOnDemand)
                    put("continuous", startParameter.isContinuous)
                    put("continueOnFailure", startParameter.isContinueOnFailure)
                    put("dryRun", startParameter.isDryRun)
                    put("offline", startParameter.isOffline)
                    put("buildScan", startParameter.isBuildScan)
                    put("noBuildScan", startParameter.isNoBuildScan)
                    put("profile", startParameter.isProfile)
                    put("refreshDependencies", startParameter.isRefreshDependencies)
                    put("rerunTasks", startParameter.isRerunTasks)
                    put("searchUpwards", startParameter.isSearchUpwards)
                    put("useEmptySettings", startParameter.isUseEmptySettings)
                    put("parallel", startParameter.isParallelProjectExecutionEnabled)
                    put("daemon", isDaemon())
                })
                put("maxWorkerCount", startParameter.maxWorkerCount)

            }
        }
    }

    private fun isDaemon(): Boolean? {
        // https://stackoverflow.com/questions/23265217/how-to-know-whether-you-are-running-inside-a-gradle-daemon
        return Thread.getAllStackTraces().keys.any { it.name.contains("Daemon") }
    }

    override fun writeResultTo(buildMonitorResult: BuildMonitorResult) {
        buildMonitorResult.values["gradle"] = result
    }
}