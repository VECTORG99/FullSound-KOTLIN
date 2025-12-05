import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.grupo8.fullsound"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.grupo8.fullsound"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        testInstrumentationRunnerArguments["clearPackageData"] = "false"

        vectorDrawables {
            useSupportLibrary = true
        }

        // Leer variables desde local.properties primero
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { stream ->
                localProperties.load(stream)
            }
        }

        // Leer variables del archivo .env (sobrescribe local.properties si existe)
        val envFile = rootProject.file(".env")
        if (envFile.exists()) {
            val lines = envFile.readLines()
            for (line in lines) {
                if (line.isNotBlank() && !line.startsWith("#") && line.contains("=")) {
                    val (key, value) = line.split("=", limit = 2)
                    buildConfigField("String", key.trim(), "\"${value.trim()}\"")
                }
            }
        } else {
            // Si no existe .env, usar local.properties o valores por defecto
            val supabaseUrl = localProperties.getProperty("SUPABASE_URL", "")
            val supabaseAnonKey = localProperties.getProperty("SUPABASE_ANON_KEY", "")
            val fixerApiKey = localProperties.getProperty("FIXER_API_KEY", "default_key")

            buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
            buildConfigField("String", "FIXER_API_KEY", "\"$fixerApiKey\"")
        }

        // Configurar BACKEND_BASE_URL
        val backendBaseUrl = localProperties.getProperty("BACKEND_BASE_URL", "http://10.0.2.2:8080/api/")
        buildConfigField("String", "BACKEND_BASE_URL", "\"$backendBaseUrl\"")
    }

    testOptions {
        animationsDisabled = true
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    signingConfigs {
        create("release") {
            // Los valores se inyectan desde GitHub Actions o local.properties
            storeFile = file(System.getProperty("android.injected.signing.store.file") ?: "release.jks")
            storePassword = System.getProperty("android.injected.signing.store.password") ?: ""
            keyAlias = System.getProperty("android.injected.signing.key.alias") ?: ""
            keyPassword = System.getProperty("android.injected.signing.key.password") ?: ""
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Supabase
    implementation(libs.supabase.postgrest.kt)
    implementation(libs.supabase.realtime.kt)
    implementation(libs.supabase.storage.kt)

    // Ktor (requerido por Supabase)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.utils)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Image Loading (Coil para cargar imágenes desde URLs de Supabase)
    implementation("io.coil-kt:coil:2.5.0")

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.robolectric:robolectric:4.11.1")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.fragment:fragment-testing:1.6.2")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
