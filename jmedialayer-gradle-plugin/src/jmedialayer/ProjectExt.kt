package jmedialayer

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.api.Task
import java.util.*

fun Project.addTask(name: String, dependsOn: List<String> = listOf(), action: (GradlePluginTask) -> Unit = {}): Task {
	class LambdaClosure<T>(private val action: (T) -> Unit) : Closure<Void>(Any()) {

		fun doCall(argument: T) {
			action(argument)
		}

		override fun getProperty(property: String): Any {
			return "lambda"
		}
	}

	val map = HashMap<String, Any>()
	map.put("type", GradlePluginTask::class.java)
	//map.put("group", "")
	//map.put("description", "")
	map.put("overwrite", true)
	val task = this.task(map, name, LambdaClosure({ it: GradlePluginTask ->
		it.action = action
		//System.out.println("********GradlePlugin configuring: " + value);
	}))
	task.setDependsOn(dependsOn)
	return task

	//this.project.task()
}

fun <T> Project.getIfExists(name: String): T? {
	return if (this.hasProperty(name)) this.property(name) as T else null
}
