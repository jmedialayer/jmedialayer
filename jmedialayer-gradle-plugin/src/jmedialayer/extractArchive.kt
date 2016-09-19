package jmedialayer

import com.jtransc.error.invalidOp
import org.codehaus.plexus.archiver.tar.TarBZip2UnArchiver
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver
import org.codehaus.plexus.archiver.zip.ZipUnArchiver
import java.io.File

fun extractArchive(archive: File, outputFolder: File) {
	val unarchiver = if (archive.path.toLowerCase().endsWith(".zip")) {
		ZipUnArchiver()
	} else if (archive.path.toLowerCase().endsWith(".tar.gz")) {
		TarGZipUnArchiver()
	} else if (archive.path.toLowerCase().endsWith(".tar.bz2")) {
		TarBZip2UnArchiver()
	} else {
		invalidOp("Don't know how to extract $archive to $outputFolder")
	}

	unarchiver.sourceFile = archive
	unarchiver.destDirectory = outputFolder
	unarchiver.extract()
}