
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")   // keep ONE Kotlin plugin
    kotlin("kapt")                       // use the KTS form for kapt
    id("com.google.gms.google-services") // Firebase services plugin
}

android {
    namespace = "com.example.movie_ticket_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.movie_ticket_app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Optional: keep this if you previously had old support libs around
    configurations.all { exclude(group = "com.android.support") }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures { viewBinding = true }
}

dependencies {
    // --- AndroidX / UI ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.activity:activity:1.12.1")

    // --- Tests ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // --- Glide (use EITHER version-catalog OR fixed version, not both) ---
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    // --- Firebase (use the BoM from your version catalog) ---
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")

    // --- Your other libs ---
    implementation(libs.chip.navigation.bar)

    // BlurView (keep your existing version)
    implementation("com.github.Dimezis:BlurView:version-2.0.6")
}
