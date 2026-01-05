
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
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

    // You can keep the exclusion to avoid old support libs leaking in
    configurations.all {
        exclude(group = "com.android.support")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures { viewBinding = true }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.activity:activity:1.12.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.glide)
    kapt(libs.glide.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)

    implementation(libs.chip.navigation.bar)

    // —— BlurView: keep ONLY v3.x (includes RenderEffectBlur) ——
    implementation("com.github.Dimezis:BlurView:version-2.0.6")

}
