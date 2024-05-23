import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.kotlin.KotlinConstants

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.google.gms) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.baseline.profile) apply false
}

subprojects {
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)

    extensions.configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude(
                "**/build/**/*.kt",                  // Build directory
                "**/org/openapitools/client/**/*.kt" // OpenAPI generated code
            )
            ktlint()
                .editorConfigOverride(
                    mapOf(
                        "ktlint_standard_max-line-length" to "disabled"
                    )
                )
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(rootProject.file("$rootDir/spotless/copyright.kt"))
        }
        format("openapiGenerated") {
            target("**/*.kt")
            targetExclude("**/build/**/*.kt")
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(
                rootProject.file("$rootDir/spotless/copyright.kt"),
                KotlinConstants.LICENSE_HEADER_DELIMITER
            )
        }
        format("kts") {
            target("**/*.kts")
            targetExclude("**/build/**/*.kts")
            // Look for the first line that doesn't have a block comment (assumed to be the license)
            licenseHeaderFile(
                rootProject.file("spotless/copyright.kts"),
                "(^(?![\\/ ]\\*).*$)"
            )
        }
        format("xml") {
            target("**/*.xml")
            targetExclude("**/build/**/*.xml")
            // Look for the first XML tag that isn't a comment (<!--) or the xml declaration (<?xml)
            licenseHeaderFile(rootProject.file("spotless/copyright.xml"), "(<[^!?])")
        }
    }
}
