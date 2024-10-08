plugins {
    kotlin("jvm") version "2.0.10"
}

group = "dev.defvs"
version = "0.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
}

tasks.test {
    enabled = false
}

kotlin {
    jvmToolchain(17)
}