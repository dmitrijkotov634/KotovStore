buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.13")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.5")
    }
}

plugins {
    id("com.android.application") version "7.2.2" apply false
    id("com.android.library") version "7.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}