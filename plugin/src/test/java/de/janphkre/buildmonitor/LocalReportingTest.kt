package de.janphkre.buildmonitor

import de.janphkre.buildmonitor.util.BaseTest
import de.janphkre.buildmonitor.util.JsonStructureVerifier
import de.janphkre.buildmonitor.util.ResourceFile
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class LocalReportingTest: BaseTest() {


    @Test
    @Throws(IOException::class)
    fun helloWorld_withLocalReporter_generatesFile() {
        writeTestBuildFile(ResourceFile.HELLO_WORLD)
        runBuild()

        val verifier = JsonStructureVerifier(getReportContent())
        verifier.verifyBase()
        verifier.map.let { result ->
            assertEquals("helloWorld", result.access("gradle").access("taskNames").access(0))
            assertEquals("SUCCESS", result.access("result").access("status"))
            assertFalse(result.access("gradle").access("switches").access("daemon") as Boolean)
            assertFalse(result.access("gradle").access("switches").access("parallel") as Boolean)
            assertTrue(result.access("gradle").access("switches").access("buildDependencies") as Boolean)
        }
    }

    @Test
    @Throws(IOException::class)
    fun failingBuild_withLocalReporter_generatesFile() {
        writeTestBuildFile(ResourceFile.FAILING)
        try {
            runBuild()
        } catch(ignored: UnexpectedBuildFailure) { /*We were expecting this failure*/ }

        val verifier = JsonStructureVerifier(getReportContent())
        verifier.verifyBaseFailure()
        verifier.map.let { result ->
            assertEquals("helloWorld", result.access("gradle").access("taskNames").access(0))
            assertEquals("FAILURE", result.access("result").access("status"))
        }
    }

    @Test
    @Throws(IOException::class)
    fun grammarError_withLocalReporter_generatesFile() {
        writeTestBuildFile(ResourceFile.GRAMMAR_ERROR)
        try {
            runBuild()
        } catch(ignored: UnexpectedBuildFailure) { /*We were expecting this failure*/ }

        val verifier = JsonStructureVerifier(getReportContent())
        verifier.verifyBaseFailure()
        verifier.map.let { result ->
            assertEquals("helloWorld", result.access("gradle").access("taskNames").access(0))
            assertEquals("FAILURE", result.access("result").access("status"))
        }
    }

    @Test
    @Throws(IOException::class)
    fun dependenciesBuild_withLocalReporter_generatesFile() {
        writeTestBuildFile(ResourceFile.DEPENDENCIES)
        runBuild()

        val verifier = JsonStructureVerifier(getReportContent())
        verifier.verifyBase()
        verifier.verifyConfigurations()
        verifier.map.let { result ->
            assertEquals("helloWorld", result.access("gradle").access("taskNames").access(0))
            assertEquals("SUCCESS", result.access("result").access("status"))
        }
    }

    @Test
    @Throws(IOException::class)
    fun javaBuild_withLocalReporter_generatesFile() {
        writeTestBuildFile(ResourceFile.JAVA_BUILD_GRADLE)
        writeTestProjectFile(ResourceFile.JAVA_BUILD_CLASS, "src/main/java/Something.java")
        runBuild("build")

        val verifier = JsonStructureVerifier(getReportContent())
        verifier.verifyBase()
        verifier.verifyConfigurations()
        verifier.verifyConfigurationDependencies()
        verifier.map.let { result ->
            assertEquals("build", result.access("gradle").access("taskNames").access(0))
            assertEquals("SUCCESS", result.access("result").access("status"))
            assertEquals("androidx.core:core:1.0.0", result.access("configurations").access(4).access("resolvedDependencies").access(0).access("name"))
        }
    }
}
