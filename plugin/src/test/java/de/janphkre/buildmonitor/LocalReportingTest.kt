package de.janphkre.buildmonitor

import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

open class LocalReportingTest {
    @get:Rule
    val testProjectDir = TemporaryFolder()

    private lateinit var buildFile: File

    private val helloWorldBuildFileContent: String
    get()  = "plugins { id 'de.janphkre.buildmonitor' }; task helloWorld { doLast { println 'Hello world!' } }"

    @Before
    @Throws(IOException::class)
    fun setup() {
        buildFile = testProjectDir.newFile("build.gradle")
        BufferedWriter(FileWriter(buildFile)).use {
            it.write(helloWorldBuildFileContent)
        }
    }

    @Test
    @Throws(IOException::class)
    fun helloWorld_withLocalReporter_generatesFile() {
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
        TODO("CHECK FILE CONTENT? $fileContent")
    }
}