package de.janphkre.buildmonitor

import org.gradle.api.NonExtensible

@NonExtensible
interface BuildMonitorDsl {

    fun followRedirects(follow: Boolean)
    fun serverUrl(url: String)
    //TODO: Provide possibility to use Basic Auth
}