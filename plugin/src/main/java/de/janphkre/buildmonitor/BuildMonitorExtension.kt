package de.janphkre.buildmonitor

import org.gradle.api.NonExtensible
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

@NonExtensible
open class BuildMonitorExtension
@javax.inject.Inject constructor(
objects: ObjectFactory
) {

    val serverUrl: Property<String?>  = objects.property(String::class.java)

    fun serverUrl(newUrl: String) {
        serverUrl.value(newUrl)
    }

    fun getFinalServerUrl(): String? {
        return serverUrl.orNull
    }
}