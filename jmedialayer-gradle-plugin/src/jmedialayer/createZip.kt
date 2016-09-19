package jmedialayer

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun createZip(vararg maps: Pair<String, String>, outputPath: String = "output.vpk", log: (String) -> Unit = {}) {
	FileOutputStream(outputPath).use { oss ->
		ZipOutputStream(oss).use { out ->
			out.setLevel(Deflater.DEFAULT_COMPRESSION)

			val written = hashSetOf<String>()

			fun writeFile(file: File, to2: String) {
				val to = to2.replace('\\', '/').trimStart('/')
				if (to in written) return // skipping: repeated
				written += to
				if (!file.exists()) return
				if (file.isDirectory) return
				log("Writting $file to $to...")
				out.putNextEntry(ZipEntry(to))
				FileInputStream(file).use { iss ->
					iss.copyTo(out)
				}
				out.closeEntry()
			}

			for ((from, to) in maps) {
				val fromFile = File(from)
				if (fromFile.isDirectory) {
					for (ff in fromFile.listRecursively()) {
						val relative = ff.relativeTo(fromFile)
						writeFile(ff, "$to/${relative.path}")
					}
				} else {
					writeFile(fromFile, "$to/${fromFile.name}")
				}
			}
		}
	}
}

fun File.listRecursively(): List<File> {
	val out = arrayListOf<File>()
	for (item in this.listFiles()) {
		if (item.isDirectory) {
			out += item.listRecursively()
		} else {
			out += item
		}
	}
	return out
}