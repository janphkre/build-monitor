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
    val result = HashMap<String, String>()

    private val jsonString by lazy {
        StringBuilder()
            .append("{\"environment\":{")
            .writeWithHash(Hashable.ENVIRONMENT, environment)
            .append("},\"result\":{")
            .writeMap(result)
            .append("}}")
            .toString()
    }

    private fun StringBuilder.writeWithHash(hashKey: Hashable, map: Map<String, String>): StringBuilder {
        val hashCode = map.hashCode()
        this.append("\"hashCode\":$hashCode")
        if(hashCode == Hashing.previousHash(hashKey)) {
            return this
        }
        Hashing.newHash(hashKey, hashCode)
        this.append(',')
        return writeMap(map)
    }

    private fun StringBuilder.writeMap(map: Map<String, String>): StringBuilder {
        map.entries.joinTo(this, separator = ",") { "\"${it.key}\":\"${it.value}\"" }
        return this
    }

    /**
     * Serializes this result into a JSON string.
     */
    override fun toString(): String {
        return jsonString
    }
}