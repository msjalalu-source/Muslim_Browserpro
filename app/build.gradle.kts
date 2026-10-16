plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.muslim.browser.pro"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.muslim.browser.pro"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("debugConfig") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        val releaseKeystorePath = System.getenv("RELEASE_STORE_FILE")
        val releaseStorePassword = System.getenv("RELEASE_STORE_PASSWORD")
        val releaseKeyAlias = System.getenv("RELEASE_KEY_ALIAS")
        val releaseKeyPassword = System.getenv("RELEASE_KEY_PASSWORD")

        if (!releaseKeystorePath.isNullOrBlank() &&
            !releaseStorePassword.isNullOrBlank() &&
            !releaseKeyAlias.isNullOrBlank() &&
            !releaseKeyPassword.isNullOrBlank() &&
            file(releaseKeystorePath).exists()
        ) {
            create("release") {
                storeFile = file(releaseKeystorePath)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debugConfig")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debugConfig")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }
}

val filteredPrebuiltJar = tasks.register<Jar>("filteredPrebuiltJar") {
    archiveFileName.set("app-prebuilt-filtered.jar")
    destinationDirectory.set(layout.buildDirectory.dir("filtered-libs"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(zipTree(file("libs/app-prebuilt.jar"))) {
        exclude("com/muslim/browser/pro/browser/ui/BottomNavBar*")
    }
}

dependencies {
    implementation(files(filteredPrebuiltJar.flatMap { it.archiveFile }))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"), "exclude" to listOf("app-prebuilt.jar"))))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.espresso.core)
    testImplementation("org.robolectric:robolectric:4.14.1")
}
