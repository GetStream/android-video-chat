/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("UnstableApiUsage")

import io.getstream.android.video.chat.compose.Configuration
import java.io.FileInputStream
import java.util.*

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.firebase.crashlytics.get().pluginId)
    id(libs.plugins.kotlin.serialization.get().pluginId)
    id(libs.plugins.hilt.get().pluginId)
    id(libs.plugins.ksp.get().pluginId)
    id(libs.plugins.spotless.get().pluginId)
    id(libs.plugins.baseline.profile.get().pluginId)
    id("com.google.gms.google-services")
}

android {
    namespace = "io.getstream.android.video.chat.compose"
    compileSdk = Configuration.compileSdk

    defaultConfig {
        applicationId = "io.getstream.android.video.chat.compose"
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
        versionCode = Configuration.versionCode
        versionName = Configuration.versionName
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }

    val signFile: File = rootProject.file(".sign/keystore.properties")
    if (signFile.exists()) {
        val properties = Properties()
        properties.load(FileInputStream(signFile))

        signingConfigs {
            create("release") {
                keyAlias = properties["keyAlias"] as? String
                keyPassword = properties["keyPassword"] as? String
                storeFile = rootProject.file(properties["keystore"] as String)
                storePassword = properties["storePassword"] as? String
            }
        }
    } else {
        signingConfigs {
            create("release") {
                keyAlias = "androiddebugkey"
                keyPassword = "android"
                storeFile = rootProject.file(".sign/debug.keystore.jks")
                storePassword = "android"
            }
        }
    }

    signingConfigs {
        getByName("debug") {
            keyAlias = "androiddebugkey"
            keyPassword = "android"
            storeFile = rootProject.file(".sign/debug.keystore.jks")
            storePassword = "android"
        }
    }

    buildTypes {
        getByName("debug") {
            versionNameSuffix = "-DEBUG"
            applicationIdSuffix = ".debug"
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        create("benchmark") {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            proguardFiles("benchmark-rules.pro")
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("development") {
            dimension = "environment"
            applicationIdSuffix = ".dogfooding"
        }
        create("production") {
            dimension = "environment"
        }
    }

    buildFeatures {
        resValues = true
        buildConfig = true
    }

    packaging {
        jniLibs.pickFirsts.add("lib/*/librenderscript-toolkit.so")
    }

    lint {
        abortOnError = false
    }

    baselineProfile {
        mergeIntoMain = true
    }
}

dependencies {
    // Stream Video SDK
    implementation(libs.stream.video.compose)
    implementation(libs.stream.video.filter)
    implementation(libs.stream.video.previewdata)

    // Stream Chat SDK
    implementation(libs.stream.chat.compose)
    implementation(libs.stream.chat.offline)
    implementation(libs.stream.chat.state)
    implementation(libs.stream.chat.ui.utils)

    implementation(libs.stream.push.firebase)
    implementation(libs.stream.log.android)

    implementation(libs.androidx.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)

    // Network
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.converter)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.hilt.navigation)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.accompanist.permission)
    implementation(libs.landscapist.coil)

    // QR code scanning
    implementation(libs.androidx.camera.core)
    implementation(libs.play.services.mlkit.barcode.scanning)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.camera2)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.config)
    implementation(libs.firebase.analytics)

    // Moshi
    implementation(libs.moshi.kotlin)

    // Video Filters
    implementation(libs.google.mlkit.selfie.segmentation)
    implementation(files("libs/renderscript-toolkit.aar"))

    // Play
    implementation(libs.play.auth)
}