package dev.defvs.heliosdac

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object HeliosJNILoader {
    init {
        loadNativeLibraries()
    }

    private fun loadNativeLibraries() {
        // Determine the OS and architecture to find the correct native library
        val os = System.getProperty("os.name").lowercase()
        val arch = System.getProperty("os.arch").lowercase()
        val libPrefix = if (os.contains("win")) "" else "lib" // Windows libraries don't have "lib" prefix
        val libExtension = when {
            os.contains("win") -> "dll"
            os.contains("mac") -> "dylib"
            else -> "so"
        }

        // Paths to the native libraries within the JAR resources
        val libHeliosDacName = "/native/${libPrefix}HeliosDac.$libExtension"
        val libHeliosJNIName = "/native/${libPrefix}HeliosJNI.$libExtension"

        // Extract the libraries to a temporary directory
        val tempDir = Files.createTempDirectory("helios_native_libs").toFile()
        val heliosDacFile = extractLibrary(libHeliosDacName, tempDir)
        val heliosJNIFile = extractLibrary(libHeliosJNIName, tempDir)

        // Load the extracted libraries in the correct order
        System.load(heliosDacFile.absolutePath)
        System.load(heliosJNIFile.absolutePath)
    }

    private fun extractLibrary(libResourcePath: String, tempDir: File): File {
        val libName = libResourcePath.split("/").last() // Extract library name from the path
        val tempFile = File(tempDir, libName)

        // Copy the library from the JAR resources to the temp directory
        HeliosJNILoader::class.java.getResourceAsStream(libResourcePath)!!.use { input ->
            Files.copy(input, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        tempFile.deleteOnExit() // Clean up on exit
        return tempFile
    }
}
