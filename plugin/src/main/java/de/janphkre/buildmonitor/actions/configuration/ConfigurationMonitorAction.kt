package de.janphkre.buildmonitor.actions.configuration

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.actions.IBuildMonitorAction
import de.janphkre.buildmonitor.result.BuildMonitorResult
import de.janphkre.buildmonitor.util.EscapingJsonWriter
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.artifacts.UnresolvedDependency

class ConfigurationMonitorAction: IBuildMonitorAction {

    private var result : List<Map<String, Any?>>? = null

    override fun monitor(target: Project, dslExtension: BuildMonitorExtension) {
        result = target.configurations.asMap.map { entry ->
            HashMap<String, Any?>(6).apply {
                put("name", entry.value.name)
                put("state", entry.value.state.name)
                // Unresolved only means that the configuration was not used for the current build:
                if(entry.value.state != Configuration.State.UNRESOLVED) {
                    val lenientConfiguration = entry.value.resolvedConfiguration.lenientConfiguration
                    putNonEmpty("resolvedDependencies", lenientConfiguration.firstLevelModuleDependencies, ::monitorDependency)
                    putNonEmpty("unresolvedDependencies", lenientConfiguration.unresolvedModuleDependencies, ::monitorDependency)
                    putNonEmpty("dependencyConstraints", entry.value.dependencyConstraints) { constraint ->
                        HashMap<String, Any?>(4).apply {
                            put(DEPENDENCY_GROUP_KEY, constraint.group)
                            put(DEPENDENCY_NAME_KEY, constraint.name)
                            constraint.version?.let { put(DEPENDENCY_VERSION_KEY, it) }
                            constraint.reason?.let { put("reason", it) }
                        }
                    }
                    putNonEmpty("forcedDependencies", entry.value.resolutionStrategy.forcedModules) { dependency ->
                        HashMap<String, Any?>(3).apply {
                            put(DEPENDENCY_GROUP_KEY, dependency.group)
                            put(DEPENDENCY_NAME_KEY, dependency.name)
                            dependency.version?.let { put(DEPENDENCY_VERSION_KEY, it) }
                        }
                    }
                }
                putNonEmpty("extendsFrom", entry.value.extendsFrom) { extendedConfiguration ->
                    extendedConfiguration.name
                }
            }
        }
    }

    private fun monitorDependency(dependency: ResolvedDependency): HashMap<String, Any?> {
        return HashMap<String, Any?>(5).apply {
            put(DEPENDENCY_NAME_KEY, dependency.name)
            dependency.children.let {
                if(it.isNotEmpty()) {
                    put("transitive", it.map(::monitorDependency))
                }
            }
        }
    }

    private fun monitorDependency(dependency: UnresolvedDependency): HashMap<String, Any?> {
        return HashMap<String, Any?>(4).apply {
            put("problem", EscapingJsonWriter.writeFailure(dependency.problem))
            val selector = dependency.selector
            put(DEPENDENCY_GROUP_KEY, selector.group)
            put(DEPENDENCY_NAME_KEY, selector.name)
            put(DEPENDENCY_VERSION_KEY, selector.version)
        }
    }

    private fun <T> HashMap<String, Any?>.putNonEmpty(key: String, list: Collection<T> , listMapper: (T) -> Any?) {
        if(list.isNotEmpty()) {
            put(key, list.map(listMapper))
        }
    }

    override fun writeResultTo(buildMonitorResult: BuildMonitorResult) {
        buildMonitorResult.values["configurations"] = result
    }

    companion object {
        private const val DEPENDENCY_GROUP_KEY = "group"
        private const val DEPENDENCY_NAME_KEY = "name"
        private const val DEPENDENCY_VERSION_KEY = "version"
    }
}