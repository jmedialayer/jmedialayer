package jmedialayer

import com.jtransc.error.invalidOp
import org.codehaus.plexus.archiver.tar.TarBZip2UnArchiver
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver
import org.codehaus.plexus.archiver.zip.ZipUnArchiver
import java.io.File

fun extractArchive(archive: File, outputFolder: File) {
	if (archive.path.toLowerCase().endsWith(".zip")) {
		ZipUnArchiver(archive).extract("", outputFolder)
	} else if (archive.path.toLowerCase().endsWith(".tar.gz")) {
		TarGZipUnArchiver(archive).extract("", outputFolder)
	} else if (archive.path.toLowerCase().endsWith(".tar.bz2")) {
		TarBZip2UnArchiver(archive).extract("", outputFolder)
	} else {
		invalidOp("Don't know how to extract $archive to $outputFolder")
	}
}