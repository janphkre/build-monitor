package de.janphkre.buildmonitor.report

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.BuildMonitorResult
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import java.net.URI

class ServerReporter(
    dslExtension: BuildMonitorExtension
): IReporter {

    private val reportUri = URI.create(dslExtension.serverUrl!!).resolve("/api/v1/builds")
    private val httpClient = HttpClients.custom().apply {
        if(!dslExtension.followRedirects) {
            disableRedirectHandling()
        }
    }.build()

    override fun report(buildMonitorResult: BuildMonitorResult) {
        val post = HttpPost(reportUri)
//TODO: CHECK IF NECESSARY:        post.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
        post.entity = StringEntity(buildMonitorResult.toString(), ContentType.APPLICATION_JSON)
        httpClient.execute(post)
    }

}