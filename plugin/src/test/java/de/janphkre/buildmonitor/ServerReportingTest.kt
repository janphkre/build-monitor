package de.janphkre.buildmonitor

import okhttp3.mockwebserver.MockWebServer
import org.apache.http.entity.ContentType
import org.gradle.internal.impldep.com.amazonaws.services.s3.Headers
import org.gradle.testkit.runner.GradleRunner
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class ServerReportingTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    private lateinit var buildFile: File

    private val helloWorldBuildFileContent: String
        get()  = "plugins { id 'de.janphkre.buildmonitor' };" +
                "buildMonitor { serverUrl \"${mockWebServer.url("")}\" };" +
                "task helloWorld { doLast { println 'Hello world!' } }"

    private lateinit var mockWebServer: MockWebServer

    @Before
    @Throws(IOException::class)
    fun setup() {
        mockWebServer = MockWebServer()
        buildFile = testProjectDir.newFile("build.gradle")
        BufferedWriter(FileWriter(buildFile)).use {
            it.write(helloWorldBuildFileContent)
        }
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    @Throws(IOException::class)
    fun helloWorld_withServerReporter_sendsRequest() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("helloWorld", "--stacktrace")
            .withPluginClasspath()
            .withDebug(true)
            .build()
        println(result.output)

        assertEquals(1, mockWebServer.requestCount)
        val request = mockWebServer.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("api/v1/builds", request.path)
        assertEquals(ContentType.APPLICATION_JSON.toString(), request.getHeader(Headers.CONTENT_TYPE))
        TODO("Check body.")

    }
}