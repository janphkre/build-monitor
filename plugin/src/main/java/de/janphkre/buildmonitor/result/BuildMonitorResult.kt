package de.janphkre.buildmonitor.result

/**
 * Complete result of the monitoring, ready to be sent off / shared.
 * It can be serialized by calling toString() on it which will create a JSON string of it.
 * The resulting string will not contain values that were already contained the last time a build
 * was monitored, effectively only showing differences during builds.
 * This diff is achieved by creating and storing hash codes for the individual fields of this class.
 * Hash codes are maintained by the de.janphkre.buildmonitor.util.Hashing object.
 */
class BuildMonitorResult {

    val values = HashMap<String, Any?>()

    private val jsonString by lazy {
        StringBuilder(2048).apply {
            values.writeMapTo(this)
        }.toString()
    }

    private fun Any?.writeAnyTo(builder: StringBuilder) {
        when(this) {
            is Map<*, *> -> this.writeMapTo(builder)
            is Iterable<*> -> this.writeListTo(builder)
            is Boolean, is Int, is Long, is Float, is Double, null -> builder.append(this.toString())
            else -> {
                builder.append('"')
                builder.append(this.toString())
                builder.append('"')
            }
        }
    }

    private fun Map<*, *>.writeMapTo(builder: StringBuilder) {
        builder.append('{')
        for ((count, element) in entries.withIndex()) {
            if (count + 1 > 1) builder.append(',')
            builder.append('"')
            builder.append(element.key.toString())
            builder.append("\":")
            element.value.writeAnyTo(builder)
        }
        builder.append('}')
    }

    private fun Iterable<*>.writeListTo(builder: StringBuilder) {
        builder.append('[')
        for ((count, element) in this.withIndex()) {
            if (count + 1 > 1) builder.append(',')
            element.writeAnyTo(builder)
        }
        builder.append(']')
    }

    /**
     * Serializes this result into a JSON string.
     */
    override fun toString(): String = jsonString
}