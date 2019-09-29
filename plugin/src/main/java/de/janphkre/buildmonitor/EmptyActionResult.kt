package de.janphkre.buildmonitor

object EmptyActionResult: IBuildMonitorActionResult {
    override fun writeTo(buildMonitorResult: BuildMonitorResult): BuildMonitorResult {
        return buildMonitorResult
    }
}