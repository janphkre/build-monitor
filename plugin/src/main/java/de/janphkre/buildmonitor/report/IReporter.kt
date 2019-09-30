package de.janphkre.buildmonitor.report

import de.janphkre.buildmonitor.BuildMonitorResult
import java.io.File

interface IReporter {

    fun report(buildMonitorResult: BuildMonitorResult)

    companion object {

        fun reportFor(serverUrl: String?, buildDir: File): IReporter {
            return if(serverUrl == null) {
                LocalReporter(buildDir)
            } else {
                ServerReporter(serverUrl)
            }
        }
    }
}