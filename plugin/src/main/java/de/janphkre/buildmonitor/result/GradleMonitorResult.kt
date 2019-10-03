package de.janphkre.buildmonitor.result

class GradleMonitorResult(
    private val excludedTasks: Set<String>,
    private val taskNames: List<String>,
    private val initScripts: List<String>,
    private val switches: List<Pair<String,Boolean>>
): PartialResult() {

    private val jsonString by lazy {
        StringBuilder()
            .append("{\"excludedTasks\":[")
            .writeList(excludedTasks)
            .append("],\"taskNames\":[")
            .writeList(taskNames)
            .append("],\"initScripts\":[")
            .writeList(initScripts)
            .append("],\"switches\":{")
            .writeListToObject(switches)
            .append("}}")
            .toString()
    }

    override fun toString(): String = jsonString
}