package de.janphkre.buildmonitor.actions

import de.janphkre.buildmonitor.BuildMonitorExtension
import org.gradle.api.Project

interface IBuildMonitorAction {
    fun monitor(target: Project, dslExtension: BuildMonitorExtension)
    fun getResult(): IBuildMonitorActionResult?
}