package de.janphkre.buildmonitor

import de.janphkre.buildmonitor.util.Hashable
import de.janphkre.buildmonitor.util.Hashing
import java.util.*

class BuildMonitorResult {

    val environment = TreeMap<String, String>()

    private fun StringBuilder.writeWithHash(hashCode: Int, hashKey: Hashable, serializer: StringBuilder.() -> Unit): StringBuilder {
        this.append("\"hashCode\":$hashCode")
        if(hashCode == Hashing.previousHash(hashKey)) {
            return this
        }
        Hashing.newHash(hashKey, hashCode)
        this.append(',')
        serializer.invoke(this)
        return this
    }

    override fun toString(): String {
        return StringBuilder()
            .append("{\"environment\":{")
            .writeWithHash(environment.hashCode(), Hashable.ENVIRONMENT) { environment.entries.joinTo(this, separator = ",") { "\"${it.key}\":\"${it.value}\"" } }
            .append("}}")
            .toString()
    }
}