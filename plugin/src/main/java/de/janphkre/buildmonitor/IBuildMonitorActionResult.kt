package de.janphkre.buildmonitor

interface IBuildMonitorActionResult {
    fun writeTo(buildMonitorResult: BuildMonitorResult): BuildMonitorResult
}