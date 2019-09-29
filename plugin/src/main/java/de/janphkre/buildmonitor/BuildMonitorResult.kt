package de.janphkre.buildmonitor

import de.janphkre.buildmonitor.util.Hashable
import de.janphkre.buildmonitor.util.Hashing

/**
 * Complete result of the monitoring, ready to be sent off / shared.
 * It can be serialized by calling toString() on it which will create a JSON string of it.
 * The resulting string will not contain values that were already contained the last time a build
 * was monitored, effectively only showing differences during builds.
 * This diff is achieved by creating and storing hash codes for the individual fields of this class.
 * Hash codes are maintained by the de.janphkre.buildmonitor.util.Hashing object.
 */
class BuildMonitorResult {

    val environment = HashMap<String, String>()

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

    /**
     * Serializes this result into a JSON string.
     */
    override fun toString(): String {
        return StringBuilder()
            .append("{\"environment\":{")
            .writeWithHash(environment.hashCode(), Hashable.ENVIRONMENT) { environment.entries.joinTo(this, separator = ",") { "\"${it.key}\":\"${it.value}\"" } }
            .append("}}")
            .toString()
    }
}