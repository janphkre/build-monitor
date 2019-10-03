package de.janphkre.buildmonitor.result

/**
 * Complete result of the monitoring, ready to be sent off / shared.
 * It can be serialized by calling toString() on it which will create a JSON string of it.
 * The resulting string will not contain values that were already contained the last time a build
 * was monitored, effectively only showing differences during builds.
 * This diff is achieved by creating and storing hash codes for the individual fields of this class.
 * Hash codes are maintained by the de.janphkre.buildmonitor.util.Hashing object.
 */
class BuildMonitorResult: PartialResult() {

    var gradle: GradleMonitorResult? = null
    val environment = HashMap<String, String>()
    val result = HashMap<String, String>()

    private val jsonString by lazy {
        StringBuilder()
            .append("{\"environment\":{")
            .writeMapToObject(environment)
            .append('}')
            .writeNonNull("gradle", gradle)
            .append(",\"result\":{")
            .writeMapToObject(result)
            .append("}}")
            .toString()
    }

    /**
     * Serializes this result into a JSON string.
     */
    override fun toString(): String = jsonString
}