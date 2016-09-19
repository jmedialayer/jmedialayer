package jmedialayer

import org.gradle.api.Project
import java.io.File
import java.io.InputStream

object TargetPsvita {
	lateinit var project: Project

	val distsDir: File get() = project.properties["distsDir"] as File
	val JTRANSC_LIBS: List<String> get() = project.properties["JTRANSC_LIBS"] as List<String>
	val extension: JMediaLayerExtension? get() = project.getIfExists<JMediaLayerExtension>(JMediaLayerExtension.NAME)
	val VITAFTP: String get() = extension?.vitaFtp ?: System.getenv("VITAFTP") ?: "192.168.1.130"
	val VITASDK: String get() = extension?.vitaSdk ?: System.getenv("VITASDK") ?: "c:/dev/psvita"
	val NAME: String get() = extension?.name ?: "TEST"
	val VPKNAME: String get() = "$NAME.vpk"
	val TITLE_ID: String get() = extension?.titleId ?: "TITLE0000"

	fun exec(exe: String, args: List<String>) {
		val fullParams = listOf(exe) + args
		project.logger.info(fullParams.map { if (it.contains(' ')) "\"$it\"" else "$it" }.joinToString(" "))
		val pb = ProcessBuilder(fullParams)
		val p = pb.start()
		val input = p.inputStream
		val error = p.errorStream
		val charset = Charsets.UTF_8
		while (true) {
			val i = input.readAvailableChunk()
			val e = error.readAvailableChunk()
			if (i.size > 0) {
				System.out.print(i.toString(charset))
			}
			if (e.size > 0) {
				System.err.print(e.toString(charset))
			}
			if (i.size == 0 && e.size == 0 && !p.isAlive) break
			Thread.sleep(1L)
		}
		p.waitFor()
	}

	fun apply(project: Project) {
		this.project = project
		//println(project.properties)

		distsDir.mkdirs()

		//project.task(mapOf(
		//	"type" to Delete::class.java,
		//))

		project.addTask("vitaDeleteTemps") {
			//File(distsDir, "a.out").delete()
			//File(distsDir, "a.velf").delete()
			//File(distsDir, "eboot.bin").delete()
			//File(distsDir, "param.sfo").delete()
			//File(distsDir, "$VPKNAME").delete()
		}

		project.addTask("vitaBuildElf", dependsOn = listOf("vitaDeleteTemps", "gensrcCpp")) {
			exec("$VITASDK/bin/arm-vita-eabi-c++",
				listOf("-Wl,-q", "-o${distsDir}/a.out", "-O3", "-std=c++0x", "build/jtransc-cpp/program.cpp", "-Iresources/includes", "-Lresources/libs") + JTRANSC_LIBS.map { "-l${it}" }
			)
		}

		project.addTask("vitaBuildSelf", dependsOn = listOf("vitaBuildElf")) {
			exec("$VITASDK/bin/vita-elf-create", listOf("$distsDir/a.out", "$distsDir/a.velf"))
		}

		project.addTask("vitaBuildEbootBin", dependsOn = listOf("vitaBuildSelf")) {
			exec("$VITASDK/bin/vita-make-fself", listOf("$distsDir/a.velf", "$distsDir/eboot.bin"))
		}

		project.addTask("vitaBuildParamSfo") {
			exec("$VITASDK/bin/vita-mksfoex", listOf("-s", "TITLE_ID=$TITLE_ID", "$NAME", "$distsDir/param.sfo"))
		}

		project.addTask("vitaBuildVpkAlone") {
			createZip(
				"$distsDir/param.sfo" to "/sce_sys",
				"assets" to "/",
				"$distsDir/eboot.bin" to "/",
				outputPath = "$distsDir/$VPKNAME",
				log = { project.logger.info(it) }
			)
		}

		project.addTask("vitaBuildVpk", dependsOn = listOf("vitaBuildEbootBin", "vitaBuildParamSfo", "vitaBuildVpkAlone"))

		project.addTask("buildVita", dependsOn = listOf("vitaBuildVpk"))

		project.addTask("vitaUploadFtpAlone") {
			exec("curl", listOf(
				"-T", "$distsDir/eboot.bin", "-u", "any:any", "ftp://$VITAFTP:1337/ux0:/app/$TITLE_ID/eboot.bin"
			))
		}

		project.addTask("vitaInstallFtpAlone") {
			exec("curl", listOf(
				"-T", "$distsDir/$VPKNAME", "-u", "any:any", "ftp://$VITAFTP:1337/ux0:/$VPKNAME", "-Q", "-PROM ux0:/$VPKNAME"
			))
		}

		project.addTask("vitaUploadFtp", dependsOn = listOf("buildVita", "vitaUploadFtpAlone"))
		project.addTask("vitaInstallFtp", dependsOn = listOf("buildVita", "vitaInstallFtpAlone"))
	}
}

fun InputStream.readAvailableChunk(): ByteArray {
	if (this.available() <= 0) return ByteArray(0)
	val out = ByteArray(this.available())
	val readed = this.read(out, 0, out.size)
	return out.sliceArray(0 until readed)
}
