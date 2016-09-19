package jmedialayer

import com.jtransc.gradle.JTranscGradlePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

@SuppressWarnings("unused")
class GradlePlugin : Plugin<Project> {
	lateinit internal var project: Project

	override fun apply(project: Project) {
		project.plugins.apply(JTranscGradlePlugin::class.java)

		project.extensions.create(JMediaLayerExtension.NAME, JMediaLayerExtension::class.java, project)

		this.project = project

		TargetPsvita.apply(project)
	}

	interface Action1<T> {
		fun exec(value: T)
	}

	interface Action2<T1, T2> {
		fun exec(v1: T1, v2: T2)
	}
}