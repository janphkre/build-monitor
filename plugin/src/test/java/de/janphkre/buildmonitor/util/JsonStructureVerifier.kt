package de.janphkre.buildmonitor.util

import org.gradle.internal.impldep.com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import java.util.HashMap

class JsonStructureVerifier(
    jsonMonitorResult: String
) {

    val map: HashMap<*, *> = ObjectMapper().readValue(jsonMonitorResult, HashMap::class.java)

    fun verifyBaseFailure() {
        verifyBase()
        map.checkedField<Map<*, *>>("result").checkedField<String>("exception")
    }

    fun verifyBase() {
        verifyEnvironment(map.checkedField("environment"))
        verifyGradle(map.checkedField("gradle"))
        verifyProject(map.checkedField("project"))
        map.checkedField<List<*>>("configurations")
        map.checkedField<Map<*,*>>("result").checkedField<String>("status")
    }

    fun verifyConfigurations() {
        val configurations = map.checkedField<List<*>>("configurations")
        val mappedConfigurations = configurations.mapNotNull { it?.checked<Map<*,*>>() }
        assertEquals(configurations.size, mappedConfigurations.size)

        mappedConfigurations.checkedConfiguration("default")
        mappedConfigurations.checkedConfiguration("annotationProcessor")
        mappedConfigurations.checkedConfiguration("apiElements")
        mappedConfigurations.checkedConfiguration("compile")
        mappedConfigurations.checkedConfiguration("compileOnly")
        mappedConfigurations.checkedConfiguration("runtime")
        mappedConfigurations.checkedConfiguration("runtimeClasspath")
        mappedConfigurations.checkedConfiguration("runtimeElements")
        mappedConfigurations.checkedConfiguration("runtimeOnly")
        mappedConfigurations.checkedConfiguration("testAnnotationProcessor")
        mappedConfigurations.checkedConfiguration("testCompile")
        mappedConfigurations.checkedConfiguration("testCompileClasspath")
        mappedConfigurations.checkedConfiguration("testCompileOnly")
        mappedConfigurations.checkedConfiguration("testImplementation")
        mappedConfigurations.checkedConfiguration("testRuntime")
        mappedConfigurations.checkedConfiguration("testRuntimeClasspath")
        mappedConfigurations.checkedConfiguration("testRuntimeOnly")
        mappedConfigurations.checkedConfiguration("implementation")
        mappedConfigurations.checkedConfiguration("compileClasspath")
    }

    fun verifyConfigurationDependencies() {
        map.checkedField<List<*>>("configurations")
            .mapNotNull { it?.checked<Map<*,*>>() }
            .checkedConfiguration("compileClasspath")
            .checkedField<List<*>>("resolvedDependencies")
            .checkedDependencies()
    }

    private fun List<Map<*, *>>.checkedConfiguration(name: String): Map<*, *> {
        val result = firstOrNull { it.checkedField<String>("name") == name }
        assertNotNull(result)
        return result!!
    }

    private fun List<*>.checkedDependencies() {
        val mappedDependencies = this.mapNotNull { it?.checked<Map<*,*>>() }
        assertEquals(this.size, mappedDependencies.size)
        assertTrue(mappedDependencies.isNotEmpty())
        mappedDependencies.forEach {
            it.checkedField<String>("name")
        }
        val withTransitiveDependency = mappedDependencies.firstOrNull { it["transitive"] != null } ?: return
        val transitiveDependencies = withTransitiveDependency.checkedField<List<*>>("transitive")
        val mappedTransitiveDependencies = transitiveDependencies.mapNotNull { it?.checked<Map<*,*>>() }
        assertEquals(transitiveDependencies, mappedTransitiveDependencies)
        assertTrue(transitiveDependencies.isNotEmpty())
        mappedTransitiveDependencies.checkedDependencies()
    }

    private fun verifyProject(project: Map<*, *>) {
        project.checkedField<String>("projectDir")
        project.checkedField<String>("gradle")
        project.checkedField<String>("status")
        project.checkedField<String>("name")
    }

    private fun verifyEnvironment(environment: Map<*,*>) {
        environment.checkedField<String>("java.version")
        environment.checkedField<String>("java.vendor")
        environment.checkedField<String>("os.arch")
        environment.checkedField<String>("os.name")
        environment.checkedField<String>("user.name")
        environment.checkedField<String>("os.version")
        environment.checkedField<Int>("processors")
        environment.checkedField<Int>("gcTime")
    }

    private fun verifyGradle(gradle: Map<*,*>) {
        assertTrue(gradle.checkedField<List<*>>("excludedTasks").isEmpty())
        assertEquals(1, gradle.checkedField<List<*>>("taskNames").size)
        assertTrue(gradle.checkedField<List<*>>("initScripts").isEmpty())
        gradle.checkedField<Map<*,*>>("switches").forEach {
            assertTrue(it.value is Boolean)
        }
    }

    private inline fun <reified T> Any.checked(): T {
        assertTrue("Element is not of expected type: $this", this is T)
        return this as T
    }

    private inline fun <reified T> Map<*,*>.checkedField(key: String): T {
        val entry = this[key]
        assertNotNull("Could not find field for key $key!", entry)
        assertTrue("Field at $key is not of expected type!", entry is T)
        return entry as T
    }
}