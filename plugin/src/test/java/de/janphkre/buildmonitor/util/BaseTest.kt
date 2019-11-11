package de.janphkre.buildmonitor.util

import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.BufferedWriter
import java.io.File
import java.io.PrintWriter
import java.lang.IllegalArgumentException

abstract class BaseTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    protected fun createTestProjectCache() {
        val testCacheFolder = testProjectDir.newFolder("local-cache")
        writeTestProjectFile("buildCache.local.directory=\"${testCacheFolder.absolutePath}\"", "settings.gradle")
    }

    protected fun writeTestBuildFile(resourceFile: ResourceFile) {
        writeTestProjectFile(resourceFile, "build.gradle")
    }

    protected fun writeTestProjectFile(resourceFile: ResourceFile, targetFile: String): File {
        return this::class.java.classLoader.getResource(resourceFile.fileName)?.file?.let {
            File(testProjectDir.root, targetFile).parentFile.mkdirs()
            val file = testProjectDir.newFile(targetFile)
            File(it).copyTo(file, true)
            file
        } ?: throw IllegalArgumentException("No resource found for $resourceFile")
    }

    protected fun writeTestProjectFile(content: String, targetFile: String): File {
        File(testProjectDir.root, targetFile).parentFile.mkdirs()
        val file = testProjectDir.newFile(targetFile)
        BufferedWriter(PrintWriter(file)).use {
            it.write(content)
            it.flush()
        }
        return file
    }

    protected fun getLastReportContent(): String {
        val reports = File(testProjectDir.root, "build/reports/monitor").listFiles()
        val monitorReport = reports
            ?.filter { it.name.startsWith("monitor-") && it.extension == "json" }
            ?.maxBy { it.lastModified() }
        Assert.assertNotNull("No monitor report was generated in \"build/reports\"", monitorReport)

        val fileContent = monitorReport!!.readText()
        println(fileContent)
        return fileContent
    }

    protected fun runBuild(task: String = "helloWorld") {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(task, "--stacktrace")
            .withPluginClasspath()
            .withDebug(true)
            .build()
        println(result.output)
    }

    protected fun Any.access(index: Int): Any {
        return (this as List<*>)[index] ?: fail("No item at index $index")
    }

    protected fun Any.access(key: String): Any {
        return (this as Map<*, *>)[key] ?: fail("Failed to find a item with the key $key")
    }
}