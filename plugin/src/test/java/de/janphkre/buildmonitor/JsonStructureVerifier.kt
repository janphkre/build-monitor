package de.janphkre.buildmonitor

import org.gradle.internal.impldep.com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import java.util.HashMap

class JsonStructureVerifier(
    jsonMonitorResult: String
) {

    private val map = ObjectMapper().readValue(jsonMonitorResult, HashMap::class.java)

    fun verifyFailure() {
        verify("FAILURE")
        map.checkedField<Map<*, *>>("result").checkedField<String>("exception")
    }

    fun verify(expectedResultType: String = "SUCCESS") {
        verifyEnvironment(map.checkedField("environment"))
        verifyGradle(map.checkedField("gradle"))
        assertEquals(expectedResultType, map.checkedField<Map<*,*>>("result").checkedField<String>("status"))
    }

    private fun verifyEnvironment(environment: Map<*,*>) {
        environment.checkedField<String>("project.projectDir")
        environment.checkedField<String>("project.gradle")
        environment.checkedField<String>("java.version")
        environment.checkedField<String>("java.vendor")
        environment.checkedField<String>("os.arch")
        environment.checkedField<String>("project.status")
        environment.checkedField<String>("os.name")
        environment.checkedField<String>("user.name")
        environment.checkedField<String>("project.name")
        environment.checkedField<String>("os.version")
    }

    private fun verifyGradle(gradle: Map<*,*>) {
        gradle.checkedField<List<*>>("excludedTasks").let {
            assertTrue(it.isEmpty())
        }
        gradle.checkedField<List<*>>("taskNames").let {
            assertEquals(1, it.size)
            assertEquals("helloWorld", it.first())
        }
        gradle.checkedField<List<*>>("initScripts").let {
            assertTrue(it.isEmpty())
        }
        gradle.checkedField<Map<*,*>>("switches").forEach {
            assertTrue(it.value is Boolean)
        }
    }

    private inline fun <reified T> Map<*,*>.checkedField(key: String): T {
        val entry = this[key]
        assertNotNull("Could not find field for key $key!", entry)
        assertTrue("Field at $key is not of expected type!", entry is T)
        return entry as T
    }
}