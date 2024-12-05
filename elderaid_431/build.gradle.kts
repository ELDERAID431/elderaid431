import org.jetbrains.kotlin.gradle.internal.kapt.incremental.UnknownSnapshot.classpath

// Top-level build.gradle dosyasındaki buildscript bloğu
buildscript {
    repositories {
        google()  // Google repository'si
        mavenCentral()  // Maven repository'si
    }
    dependencies {
        // Android Gradle Plugin versiyonu
        classpath ("com.android.tools.build:gradle:7.0.4")

        // Kotlin Gradle Plugin versiyonu
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
    }
}
