package de.janphkre.buildmonitor.reporting

import de.janphkre.buildmonitor.result.BuildMonitorResult
import java.io.File
import java.text.DateFormat
import java.util.Date

class LocalReporter(
    private val localBuildDir: File
): IReporter {

    override fun report(buildMonitorResult: BuildMonitorResult) {
        val jsonResult = buildMonitorResult.toString()
        val timestamp = DateFormat.getDateTimeInstance().format(Date())
        File(localBuildDir, "reports/monitor").apply {
            mkdirs()
            File(this, "monitor-$timestamp.json").apply {
                createNewFile()
                bufferedWriter().use { it.write(jsonResult) }
            }
        }
    }
}