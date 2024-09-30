plugins {
    kotlin("jvm") version "2.0.10"
}

group = "dev.defvs"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("../build/libs/heliosdac-0.1.jar"))
}

tasks.test {
    enabled = false
}

kotlin {
    jvmToolchain(17)
}