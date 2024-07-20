plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {

    buildFeatures {
        buildConfig = true
    }

    namespace = "com.example.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")


    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("String", "WEATHER_API_KEY", "\"${project.findProperty("weatherApiKey")?.toString()}\"")
        }
        debug {
            buildConfigField("String", "WEATHER_API_KEY", "\"${project.findProperty("weatherApiKey")?.toString()}\"")
        }
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

    implementation("androidx.core:core-ktx:1.13.1")
    implementation(project(":domain"))
    implementation("com.squareup.retrofit2:adapter-java8:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.parceler:parceler-api:1.1.12")
    annotationProcessor("org.parceler:parceler:1.1.12")

    //dagger 2
    val daggerVer = "2.51.1"
    implementation("com.google.dagger:dagger:$daggerVer")
    kapt("com.google.dagger:dagger-compiler:$daggerVer")
    implementation("com.google.dagger:dagger-android:$daggerVer")
    kapt("com.google.dagger:dagger-android-processor:$daggerVer")
    implementation("javax.inject:javax.inject:1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
}