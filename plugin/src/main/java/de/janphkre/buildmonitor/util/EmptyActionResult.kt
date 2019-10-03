package de.janphkre.buildmonitor.util

import de.janphkre.buildmonitor.BuildMonitorResult
import de.janphkre.buildmonitor.actions.IBuildMonitorActionResult

object EmptyActionResult: IBuildMonitorActionResult {
    override fun writeTo(buildMonitorResult: BuildMonitorResult): BuildMonitorResult {
        return buildMonitorResult
    }
}