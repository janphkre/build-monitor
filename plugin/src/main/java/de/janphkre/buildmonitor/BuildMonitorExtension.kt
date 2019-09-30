package de.janphkre.buildmonitor

import org.gradle.api.NonExtensible

@NonExtensible
open class BuildMonitorExtension {
    var followRedirects: Boolean = true
    var serverUrl: String? = null
    //TODO: Provide possibility to use Basic Auth
}