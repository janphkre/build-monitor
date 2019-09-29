package de.janphkre.buildmonitor

import org.gradle.api.Project

interface IBuildMonitorAction {
    fun monitor(target: Project): IBuildMonitorActionResult
}