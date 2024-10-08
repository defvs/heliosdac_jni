plugins {
    kotlin("jvm") version "2.0.10"
    `maven-publish`
}

group = "dev.defvs"
version = "0.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

// Read the flag from gradle.properties
val buildNativeLibsEnabled = let {
    if (System.getenv("JITPACK") == "true") false // Disable building of native libs if Jitpack
    else project.findProperty("buildNativeLibs")?.toString()?.toBoolean() ?: false
}

// Paths
val heliosSdkPath = file("lib/helios_dac/sdk/cpp")
val outputDir = layout.buildDirectory.file("cbuild").get().asFile // Directory for compiled shared libraries
val resourcesDir = file("src/main/resources/native") // Target resources directory for packaging

// Task to build the native Helios DAC library
val buildHeliosDac by tasks.creating(Exec::class) {
    val heliosSourceFiles = listOf("HeliosDac.cpp", "HeliosDac.h").map { file("$heliosSdkPath/$it") }
    val libusbPath = "${heliosSdkPath}/libusb_bin/Linux x64/libusb-1.0.so"

    doFirst {
        outputDir.mkdirs()
    }

    commandLine(
        "g++", "-Wall", "-std=c++14", "-fPIC", "-O2",
        *heliosSourceFiles.map { it.absolutePath }.toTypedArray(),
        "-shared", "-o", File(outputDir, "libHeliosDac.so").absolutePath,
        libusbPath // Link against libusb
    )
}

// Task to build the JNI shared library (libHeliosJNI.so)
val buildNativeLib by tasks.creating(Exec::class) {
    val jniSourceFile = file("src/main/jni/helios_dac_jni.cpp") // Your custom JNI source file
    val targetName = "libHeliosJNI" // Name of the resulting shared library

    dependsOn(buildHeliosDac) // Ensure libHeliosDac.so is built first

    doFirst {
        outputDir.mkdirs()
    }

    commandLine(
        "g++", "-I", "${System.getenv("JAVA_HOME")}/include", // JNI include directory
        "-I", "${System.getenv("JAVA_HOME")}/include/linux",  // Platform-specific JNI includes (e.g., win32 for Windows)
        "-I", heliosSdkPath.absolutePath,                     // Include path for Helios headers
        "-fPIC", "-shared", "-std=c++14",                     // Position-independent code and shared library flags
        jniSourceFile.absolutePath,                           // Input source file
        File(outputDir, "libHeliosDac.so").absolutePath,      // Link against the generated Helios DAC library
        "-o", File(outputDir, "$targetName.so").absolutePath  // Output shared library
    )
}

// Task to copy native libraries into the resources folder
val copyNativeLibsToResources by tasks.creating(Copy::class) {
    dependsOn(buildNativeLib) // Ensure all libraries are built first

    from(outputDir) {
        include("*.so", "*.dll", "*.dylib") // Include all shared library formats
    }
    into(resourcesDir) // Copy to resources/native directory
}

tasks.compileKotlin {
    if (buildNativeLibsEnabled) dependsOn(copyNativeLibsToResources)
}

// Modify the 'processResources' task to include native libraries in the final JAR
tasks.processResources {
    if (buildNativeLibsEnabled) dependsOn(copyNativeLibsToResources)
}

tasks.named("jar") {
    dependsOn("processResources")
}

// Optional: Task to clean native build outputs
tasks.register<Delete>("cleanNativeLibs") {
    delete(outputDir)
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
