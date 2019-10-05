package de.janphkre.buildmonitor

import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildMonitorPlugin: Plugin<Project> {

    private val supportedGradleVersion = "5.4"

    override fun apply(target: Project) {
        if(!target.gradle.gradleVersion.startsWith(supportedGradleVersion)) {
            throw UnsupportedOperationException("Only gradle version $supportedGradleVersion.* is supported by this plugin version.")
        }
        val dslExtension = target.extensions.create("buildMonitor", BuildMonitorExtension::class.java, target.objects)

        //TODO: modules?!
        target.gradle.addBuildListener(BuildMonitorListener(dslExtension))
    }
}