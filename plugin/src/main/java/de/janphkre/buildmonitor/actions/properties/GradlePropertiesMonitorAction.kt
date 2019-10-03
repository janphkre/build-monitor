package de.janphkre.buildmonitor.actions.properties

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.result.BuildMonitorResult
import de.janphkre.buildmonitor.actions.IBuildMonitorAction
import de.janphkre.buildmonitor.result.GradleMonitorResult
import org.gradle.api.Project

class GradlePropertiesMonitorAction: IBuildMonitorAction {

    private var result: GradleMonitorResult? = null

    override fun monitor(target: Project, dslExtension: BuildMonitorExtension) {
        target.gradle.startParameter.let { startParameter ->
            result = GradleMonitorResult(
                startParameter.excludedTaskNames,
                startParameter.taskNames,
                startParameter.allInitScripts.map { it.absolutePath },
                listOf(
                    Pair("buildCache", startParameter.isBuildCacheEnabled),
                    Pair("buildDependencies", startParameter.isBuildProjectDependencies),
                    Pair("configureOnDemand", startParameter.isConfigureOnDemand),
                    Pair("continuous", startParameter.isContinuous),
                    Pair("continueOnFailure", startParameter.isContinueOnFailure),
                    Pair("dryRun", startParameter.isDryRun),
                    Pair("offline", startParameter.isOffline),
                    Pair("buildScan", startParameter.isBuildScan),
                    Pair("noBuildScan", startParameter.isNoBuildScan),
                    Pair("profile", startParameter.isProfile),
                    Pair("refreshDependencies", startParameter.isRefreshDependencies),
                    Pair("rerunTasks", startParameter.isRerunTasks),
                    Pair("searchUpwards", startParameter.isSearchUpwards),
                    Pair("useEmptySettings", startParameter.isUseEmptySettings)
                )
            )
        }
    }

    override fun writeResultTo(buildMonitorResult: BuildMonitorResult) {
        buildMonitorResult.gradle = result
    }
}