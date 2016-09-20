package jmedialayer

import com.jtransc.JTranscSystem
import com.jtransc.error.invalidOp
import org.gradle.api.Project
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

object TargetPsvita {
	lateinit var project: Project

	fun getVitaSdkDownloadUrl(): URL {
		return URL(if (JTranscSystem.isMac()) {
			"https://github.com/jmedialayer/sdks/releases/download/0.1/vitasdk-gcc-4.9-mac-nightly-d0ed690c623673055a63de3210812490644b4170.tar.bz2"
		} else if (JTranscSystem.isLinux()) {
			"https://github.com/jmedialayer/sdks/releases/download/0.1/vitasdk-gcc-4.9-linux-nightly-752d75fd59b15a6d75e4b2af5db1654a675dddcf.tar.bz2"
		} else if (JTranscSystem.isWindows()) {
			"https://github.com/jmedialayer/sdks/releases/download/0.1/vitasdk-gcc-4.9-win32-nightly-752d75fd59b15a6d75e4b2af5db1654a675dddcf.zip"
		} else {
			invalidOp("Unknown operating system ${JTranscSystem.getOS()}")
		})
	}

	val jmedialayerPrivateDir: File by lazy { File(System.getProperty("user.home") + "/.jmedialayer").apply { mkdirs() } }
	val jmedialayerPrivateSdkDir: File by lazy { File(jmedialayerPrivateDir, "vitasdk").apply { mkdirs() } }

	val distsDir: File get() = project.properties["distsDir"] as File
	val JTRANSC_LIBS: List<String> get() = project.properties["JTRANSC_LIBS"] as List<String>
	val extension: JMediaLayerExtension? get() = project.getIfExists<JMediaLayerExtension>(JMediaLayerExtension.NAME)
	val VITAFTP: String by lazy {
		extension?.vitaFtp ?: System.getenv("VITAFTP") ?: "192.168.1.130"
	}
	val VITASDK: String by lazy {
		val defined = extension?.vitaSdk ?: System.getenv("VITASDK") ?: "c:/dev/psvita"

		if (!File("$defined/arm-vita-eabi/lib/libc.a").exists()) {
			val url = getVitaSdkDownloadUrl()
			val localfile = File(jmedialayerPrivateDir, File(url.file).name)
			if (!localfile.exists()) {
				println("Downloading sdk $url -> $localfile...")
				url.openStream().use { urls ->
					FileOutputStream(localfile).use { oss ->
						urls.copyTo(oss)
					}
				}
				println("Extracting $localfile...")
				extractArchive(localfile, jmedialayerPrivateSdkDir)
			}
			jmedialayerPrivateSdkDir.absolutePath
		} else {
			defined
		}
	}
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
		val exit = p.waitFor()
		if (exit != 0) {
			invalidOp("Process $exe exit with $exit")
		}
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
