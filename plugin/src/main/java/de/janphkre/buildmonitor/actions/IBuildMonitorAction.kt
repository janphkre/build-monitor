package de.janphkre.buildmonitor.actions

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.result.BuildMonitorResult
import org.gradle.api.Project

interface IBuildMonitorAction {
    fun monitor(target: Project, dslExtension: BuildMonitorExtension)
    fun writeResultTo(buildMonitorResult: BuildMonitorResult)
}