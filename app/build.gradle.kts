plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.skycast"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.skycast"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core:1.13.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")
    //Fragments
    implementation("androidx.fragment:fragment-ktx:1.8.1")
    //Location
    implementation("com.google.android.gms:play-services-location:21.3.0")
    // modules
    implementation(project(":data"))
    implementation(project(":domain"))
    //Retrofit2
    implementation("com.squareup.retrofit2:adapter-java8:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    //swiperefreshlayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    //shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    //dagger 2
    implementation("com.google.dagger:dagger:2.28.3")
    kapt("com.google.dagger:dagger-compiler:2.28.3")
}