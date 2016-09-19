package jmedialayer

import org.gradle.api.Project

open class JMediaLayerExtension(val project: Project) {
	companion object {
		@JvmStatic val NAME = "jmedialayer"
	}

	var vitaFtp: String? = null
	var vitaSdk: String? = null
	var name: String? = null
	var titleId: String? = null
}