package de.janphkre.buildmonitor.actions.tasks

import de.janphkre.buildmonitor.BuildMonitorExtension
import de.janphkre.buildmonitor.actions.IBuildMonitorAction
import de.janphkre.buildmonitor.result.BuildMonitorResult
import de.janphkre.buildmonitor.util.EscapingJsonWriter
import org.gradle.api.Project
import org.gradle.internal.operations.notify.BuildOperationFinishedNotification
import org.gradle.internal.operations.notify.BuildOperationNotificationListener
import org.gradle.internal.operations.notify.BuildOperationProgressNotification
import org.gradle.internal.operations.notify.BuildOperationStartedNotification
import java.util.LinkedList

class TaskProgressMonitorAction: IBuildMonitorAction {

    private val notifications = LinkedList<Map<String ,Any>>()

    override fun monitor(target: Project, dslExtension: BuildMonitorExtension) {
        TODO("not implemented")
    }

    override fun writeResultTo(buildMonitorResult: BuildMonitorResult) {
        buildMonitorResult.values["notifications"] = notifications
    }

    inner class MonitorNotificationListener: BuildOperationNotificationListener {
        override fun progress(notification: BuildOperationProgressNotification?) {
            if(notification == null) return
            notifications.add(HashMap<String, Any>().apply {
                put(NOTIFICATION_TYPE_KEY, "progress")
                put(NOTIFICATION_TIME_KEY, notification.notificationOperationProgressTimestamp)
                put(NOTIFICATION_ID_KEY, notification.notificationOperationId)
            })
        }

        override fun finished(notification: BuildOperationFinishedNotification?) {
            if(notification == null) return
            notifications.add(HashMap<String, Any>().apply {
                put(NOTIFICATION_TYPE_KEY, "finished")
                put(NOTIFICATION_TIME_KEY, notification.notificationOperationFinishedTimestamp)
                put(NOTIFICATION_ID_KEY, notification.notificationOperationId)
                val failure = notification.notificationOperationFailure
                if(failure != null) {
                    put(NOTIFICATION_FAILURE_KEY, EscapingJsonWriter.writeFailure(failure))
                } else {
                    put(NOTIFICATION_RESULT_KEY, notification.notificationOperationResult.toString()) //TODO: CONVERT TO USABLE DATA
                }
                put(NOTIFICATION_DETAILS_KEY, notification.notificationOperationDetails.toString()) //TODO
            })
        }

        override fun started(notification: BuildOperationStartedNotification?) {
            if(notification == null) return
            notifications.add(HashMap<String, Any>().apply {
                put(NOTIFICATION_TYPE_KEY, "started")
                put(NOTIFICATION_TIME_KEY, notification.notificationOperationStartedTimestamp)
                put(NOTIFICATION_ID_KEY, notification.notificationOperationId)
                notification.notificationOperationParentId?.let { put(NOTIFICATION_PARENT_ID_KEY, it) }
                put(NOTIFICATION_DETAILS_KEY, notification.notificationOperationDetails.toString()) //TODO
            })
        }
    }

    companion object {
        private const val NOTIFICATION_TYPE_KEY = "type"
        private const val NOTIFICATION_TIME_KEY = "timestamp"
        private const val NOTIFICATION_ID_KEY = "id"
        private const val NOTIFICATION_PARENT_ID_KEY = "parent"
        private const val NOTIFICATION_FAILURE_KEY = "failure"
        private const val NOTIFICATION_RESULT_KEY = "result"
        private const val NOTIFICATION_DETAILS_KEY = "details"
    }
}