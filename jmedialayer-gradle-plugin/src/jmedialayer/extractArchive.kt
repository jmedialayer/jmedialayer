package jmedialayer

import com.jtransc.error.invalidOp
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.codehaus.plexus.archiver.zip.ZipUnArchiver
import org.gradle.api.tasks.OutputFile
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths

fun extractArchive(archive: File, outputFolder: File) {
	val lowname = archive.name.toLowerCase()
	if (lowname.endsWith(".zip")) {
		uncompressZip(archive, outputFolder)
	} else if (lowname.endsWith(".tar.gz") || lowname.endsWith(".tar.bz2") || lowname.endsWith(".tar")) {
		uncompressTar(archive, outputFolder)
	} else {
		invalidOp("Don't know how to extract $archive to $outputFolder")
	}
}

fun uncompressZip(archive: File, outputFolder: File) {
	//ZipUnArchiver(archive).extract("", dest)

	/*
	FileInputStream(archive).use { fileIS ->
		val bfileIS = BufferedInputStream(fileIS)
		ZipArchiveInputStream(bfileIS).use { tarIn ->
			var entry = tarIn.nextZipEntry
			while (entry != null) {
				val destPath = File(dest, entry.name)
				if (entry.isDirectory) {
					destPath.mkdirs()
				} else {
					destPath.parentFile.mkdirs()
					BufferedOutputStream(FileOutputStream(destPath)).use { tarIn.copyTo(it) }
				}
				entry = tarIn.nextZipEntry
			}
		}
	}
	*/

	ZipUnArchiver(archive).extract("", outputFolder)
}

fun uncompressTar(archive: File, outputFolder: File) {
	//dest.mkdirs()

	/*
	FileInputStream(archive).use { fileIS ->
		val bfileIS = BufferedInputStream(fileIS)
		val uncompressedIs = if (archive.name.toLowerCase().endsWith(".bz2")) {
			BZip2CompressorInputStream(bfileIS)
		} else if (archive.name.toLowerCase().endsWith(".gz")) {
			GzipCompressorInputStream(bfileIS)
		} else if (archive.name.toLowerCase().endsWith(".tar")) {
			bfileIS
		} else {
			invalidOp("Don't know how to uncompress $archive")
		}
		TarArchiveInputStream(uncompressedIs).use { tarIn ->
			var entry = tarIn.nextTarEntry
			while (entry != null) {
				val destPath = File(dest, entry.name)
				if (entry.isDirectory) {
					destPath.mkdirs()
				} else {
					destPath.parentFile.mkdirs()
					BufferedOutputStream(FileOutputStream(destPath)).use { tarIn.copyTo(it) }
				}
				Files.setPosixFilePermissions(Paths.get(destPath.toURI()),
					Set<PosixFilePermission> perms)
				entry.mode
				entry = tarIn.nextTarEntry
			}
		}
	}
	*/

	outputFolder.mkdirs()
	ProcessBuilder("tar", "-xvf", archive.absolutePath, "-C", outputFolder.absolutePath, "--strip", "1").inheritIO().start().waitFor()
}