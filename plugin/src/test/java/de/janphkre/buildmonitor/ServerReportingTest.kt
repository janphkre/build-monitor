package de.janphkre.buildmonitor

import de.janphkre.buildmonitor.util.BaseTest
import de.janphkre.buildmonitor.util.JsonStructureVerifier
import de.janphkre.buildmonitor.util.ResourceFile
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.nio.charset.Charset

class ServerReportingTest: BaseTest() {

    private lateinit var mockWebServer: MockWebServer

    @Before
    @Throws(IOException::class)
    fun setup() {
        mockWebServer = MockWebServer()
        writeTestProjectFile("buildMonitorServerUrl=${mockWebServer.url("")}", "gradle.properties")
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    @Throws(IOException::class)
    fun helloWorld_withServerReporter_sendsRequest() {
        writeTestBuildFile(ResourceFile.SERVER_HELLO_WORLD)
        runBuild()

        assertEquals(1, mockWebServer.requestCount)
        val request = mockWebServer.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/v1/builds", request.path)
        assertEquals("application/json; charset=utf-8", request.getHeader("Content-Type"))

        val requestBodyContent = request.body.readString(Charset.forName("UTF-8"))
        println(requestBodyContent)
        val verifier = JsonStructureVerifier(requestBodyContent)
        verifier.verifyBase()
        verifier.map.let { result ->
            assertEquals("helloWorld", result.access("gradle").access("taskNames").access(0))
            assertEquals("SUCCESS", result.access("result").access("status"))
        }
    }
}