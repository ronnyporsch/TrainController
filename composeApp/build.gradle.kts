import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    targets.all {
        compilations.all {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xexpect-actual-classes") //removes warning that expect/actual classes are in beta
                //enable explicit backing fields (experimental in Kotlin 2.3.0)
                freeCompilerArgs.add("-Xexplicit-backing-fields")
            }
        }
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        val desktopMain by getting

        //intermediate source set for jvm and android target. See https://kotlinlang.org/docs/multiplatform-hierarchy.html
        val javaMain by creating {
            dependsOn(commonMain.get())
        }
        androidMain.get().dependsOn(javaMain)
        desktopMain.dependsOn(javaMain)

        //see https://kotlinlang.org/docs/multiplatform-hierarchy.html#additional-configuration
        applyDefaultHierarchyTemplate()

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.navigation.compose)
            implementation(libs.kable.core)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.jna)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

android {
    namespace = "de.ronnyporsch.train_controller"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "de.ronnyporsch.train_controller"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildOutputs.all {
        //change .apk file name
        (this as? com.android.build.gradle.internal.api.BaseVariantOutputImpl)?.outputFileName = "TrainController-${this.name}.apk"
    }
    buildTypes {
        debug {
            versionNameSuffix = "-$name"
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug") //TODO should use a proper config instead (see https://developer.android.com/studio/publish/app-signing#sign-apk)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "de.ronnyporsch.train_controller.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "TrainController"
            packageVersion = "0.1.0"
        }
    }
}
