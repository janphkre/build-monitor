package de.janphkre.buildmonitor.actions

import de.janphkre.buildmonitor.BuildMonitorResult

interface IBuildMonitorActionResult {
    fun writeTo(buildMonitorResult: BuildMonitorResult): BuildMonitorResult
}