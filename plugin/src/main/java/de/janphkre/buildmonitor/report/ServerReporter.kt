package de.janphkre.buildmonitor.report

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.BuildMonitorResult
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.Socket
import java.net.URI

class ServerReporter(
    dslExtension: BuildMonitorExtension
): IReporter {

    private val reportUri = URI.create(dslExtension.getFinalServerUrl()!!)

    override fun report(buildMonitorResult: BuildMonitorResult) {
        println (reportUri)
        val requestBody = buildMonitorResult.toString() //TODO: CONSIDER ENCODING, CONSIDER GZIP
        val response = Socket(reportUri.host, reportUri.port).use { socket ->
            BufferedWriter(OutputStreamWriter(socket.getOutputStream())).use { writer ->
                writer.run {
                    write("POST /api/v1/builds HTTP/1.1\r\n")
                    write("Connection: close\r\n")
                    write("Content-Length: ${requestBody.length}\r\n")//TODO: IS THIS LENGTH CORRECT?
                    write("Content-Type: application/json; charset=utf-8\r\n\r\n")
                    write(requestBody)
                    flush()
                }
            }
            //TODO input throws socket closed exception...
//            InputStreamReader(socket.getInputStream()).use { reader ->
//                reader.readText()
//            }
        }

        println("Report response:\n$response")
    }
}