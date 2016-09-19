package jmedialayer

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class GradlePluginTask : DefaultTask() {
	lateinit var action: (GradlePluginTask) -> Unit
	var arg: Any? = null

	@SuppressWarnings("unused")
	@TaskAction
	internal fun task() {
		action(this)
		//System.out.println("JMediaLayer:MyTask:task");
	}
}
