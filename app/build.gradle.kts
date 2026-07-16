plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.ridemate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ridemate"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {


        // 1. Android Standard Libraries (Sirf catalog wali rakhein)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.androidx.navigation.fragment.ktx)
        implementation(libs.androidx.navigation.ui.ktx)
        implementation(libs.androidx.activity)

        // 2. UI & Animations
        implementation("com.airbnb.android:lottie:6.0.0")

        // 3. OSMDroid for maps & Location
        implementation("org.osmdroid:osmdroid-android:6.1.18")
        implementation("com.google.android.gms:play-services-location:21.0.1")
        implementation("com.github.bumptech.glide:glide:4.16.0")

        // 4. Firebase (BoM ke zariye auto-manage ho raha hai)
        implementation(platform("com.google.firebase:firebase-bom:33.1.0")) // BoM versions ko control karega
        implementation("com.google.firebase:firebase-auth-ktx")
        implementation("com.google.firebase:firebase-firestore-ktx")
        implementation("com.google.firebase:firebase-storage-ktx") // FIX: Direct string add kar di taake error na aaye

        // 4b. Coroutines tasks (.await()) ko chalane ke liye zaroori library
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")

        implementation(libs.androidx.gridlayout)

        // 5. Unit testing
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
    }

