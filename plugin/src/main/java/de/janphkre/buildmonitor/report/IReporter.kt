package de.janphkre.buildmonitor.report

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.BuildMonitorResult
import java.io.File

interface IReporter {

    fun report(buildMonitorResult: BuildMonitorResult)

    companion object {

        fun reportFor(dslExtension: BuildMonitorExtension, buildDir: File): IReporter {
            return if(dslExtension.serverUrl == null) {
                LocalReporter(buildDir)
            } else {
                ServerReporter(dslExtension)
            }
        }
    }
}