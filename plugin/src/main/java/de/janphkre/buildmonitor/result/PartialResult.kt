package de.janphkre.buildmonitor.result

abstract class PartialResult {

    protected fun StringBuilder.writeMapToObject(map: Map<String, String>): StringBuilder {
        map.entries.joinTo(this, separator = ",") { "\"${it.key}\":\"${it.value}\"" }
        return this
    }

    protected fun StringBuilder.writeList(iterable: Iterable<String>): StringBuilder {
        return append(iterable.joinToString(separator = ",") { "\"$it\"" })
    }

    protected fun StringBuilder.writeListToObject(iterable: Iterable<Pair<String, *>>): StringBuilder {
        return append(iterable.joinToString(separator = ",") { "\"${it.first}\":${it.second.toString()}" })
    }

    protected fun StringBuilder.writeNonNull(key: String, value: Any?): StringBuilder {
        return value?.let {
            this.append(",\"$key\":")
                .append(it.toString())
        } ?: this
    }
}