import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("org.owasp.dependencycheck")
}

val compose_ui_version = "1.2.1"
val compose_compiler_version = "1.3.1"

kotlin {
    explicitApi = ExplicitApiMode.Strict
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "eu.sesma.devcalc"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations.addAll(listOf("en"))
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            multiDexEnabled = true
        }

        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            multiDexEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    kapt {
        useBuildCache = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    lint {
        lintConfig = file("${rootDir}/lint.xml")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = compose_compiler_version
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

// https://jeremylong.github.io/DependencyCheck/dependency-check-gradle/configuration.html
// https://github.com/jeremylong/DependencyCheck
// run with  ./gradlew dependencyCheckAnalyze --info
// results in app/build/reports
dependencyCheck {
    suppressionFile = "config/owasp/suppressions.xml"
    failBuildOnCVSS = 0f
    scanConfigurations = configurations.filter {
        (!it.name.startsWith("androidTest") && !it.name.startsWith("test") && !it.name.startsWith("debug")) && it.name.contains(
            "DependenciesMetadata"
        ) && (it.name.startsWith("api") || it.name.startsWith("implementation") || it.name.startsWith("runtimeOnly") || it.name.contains(
            "Api"
        ) || it.name.contains("Implementation") || it.name.contains("RuntimeOnly"))
    }.map { it.name }
}
dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.5.1")

    implementation("androidx.compose.ui:ui:$compose_ui_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_ui_version")
    implementation("androidx.compose.material:material:1.2.1")

    implementation("javax.inject:javax.inject:1")
    implementation("com.google.dagger:hilt-android:2.43.2")
    kapt("com.google.dagger:hilt-compiler:2.43.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_ui_version")
    debugImplementation("androidx.compose.ui:ui-tooling:$compose_ui_version")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_ui_version")
}