package de.janphkre.buildmonitor

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class LocalReportingTest {
    @get:Rule
    val testProjectDir = TemporaryFolder()

    private lateinit var buildFile: File

    private val helloWorldBuildFileContent: String
        get()  = "plugins { id 'de.janphkre.buildmonitor' }; task helloWorld { doLast { println 'Hello world!' } }"

    private val failingBuildFileContent: String
        get()  = "plugins { id 'de.janphkre.buildmonitor' }; task helloWorld { doLast { throw new RuntimeException(\"Escaping exception message \\\"Failure-Message\\\"\") } }"

    private fun writeBuildFile(content: String) {
        buildFile = testProjectDir.newFile("build.gradle")
        BufferedWriter(FileWriter(buildFile)).use {
            it.write(content)
        }
    }

    @Test
    @Throws(IOException::class)
    fun helloWorld_withLocalReporter_generatesFile() {
        writeBuildFile(helloWorldBuildFileContent)
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("helloWorld", "--stacktrace")
            .withPluginClasspath()
            .withDebug(true)
            .build()
        println(result.output)

        val reports = File(testProjectDir.root, "build/reports/monitor").listFiles()
        val monitorReport = reports?.firstOrNull { it.name.startsWith("monitor-") && it.extension == "json" }
        assertNotNull("No monitor report was generated in \"build/reports\"", monitorReport)

        val fileContent = monitorReport!!.readText()
        println(fileContent)
        JsonStructureVerifier(fileContent).verify()
    }

    @Test
    @Throws(IOException::class)
    fun failingBuild_withLocalReporter_generatesFile() {
        writeBuildFile(failingBuildFileContent)
        try {
            GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("helloWorld", "--stacktrace")
                .withPluginClasspath()
                .withDebug(true)
                .build()
        } catch(ignored: UnexpectedBuildFailure) { /*We were expecting this failure*/ }

        val reports = File(testProjectDir.root, "build/reports/monitor").listFiles()
        val monitorReport = reports?.firstOrNull { it.name.startsWith("monitor-") && it.extension == "json" }
        assertNotNull("No monitor report was generated in \"build/reports\"", monitorReport)

        val fileContent = monitorReport!!.readText()
        println(fileContent)
        JsonStructureVerifier(fileContent).verifyFailure()
    }
}
