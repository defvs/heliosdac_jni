# HeliosDAC JNI bindings

[![](https://jitpack.io/v/defvs/heliosdac_jni.svg)](https://jitpack.io/#defvs/heliosdac_jni)

Kotlin ( / Java ) JNI bindings for [helios_dac](https://github.com/Grix/helios_dac).

Compiled shared libraries for `helios_dac` and JNI are provided (in `./src/main/resoures/native`)
but building your own is recommended, through the gradle target `./gradlew buildNativeLib`. Reusing the existing natives is as simple as running `./gradlew jar` or `./gradlew publishToMavenLocal`.

Artifacts are available through [Jitpack](https://jitpack.io/#defvs/heliosdac_jni):

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    // ...
    implementation("com.github.defvs:heliosdac_jni:0.2")
}
```

Note that only shared libraries for linux_x86_64 are provided in this artifact.