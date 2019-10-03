package de.janphkre.buildmonitor.report

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.BuildMonitorResult
import java.io.File

interface IReporter {

    fun report(buildMonitorResult: BuildMonitorResult)

    companion object {

        fun reportFor(dslExtension: BuildMonitorExtension, buildDir: File): IReporter {
            return if(dslExtension.getFinalServerUrl() == null) {
                LocalReporter(buildDir)
            } else {
                ServerReporter(dslExtension)
            }
        }
    }
}