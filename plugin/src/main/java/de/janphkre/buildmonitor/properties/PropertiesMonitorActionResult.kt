package de.janphkre.buildmonitor.properties

import de.janphkre.buildmonitor.BuildMonitorResult
import de.janphkre.buildmonitor.IBuildMonitorActionResult

class PropertiesMonitorActionResult(
    private val properties: List<Pair<String, String>>
): IBuildMonitorActionResult {

    override fun writeTo(buildMonitorResult: BuildMonitorResult): BuildMonitorResult {
        buildMonitorResult.environment.putAll(properties)
        return buildMonitorResult
    }

}