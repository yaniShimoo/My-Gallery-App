plugins {
    id("com.android.application")
}

android {
    namespace = "com.rabin.mygallery"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.rabin.mygallery"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // For Card view
    implementation ("androidx.cardview:cardview:1.0.0")

// Chart and graph library
    implementation ("com.github.blackfizz:eazegraph:1.2.5l@aar")
    implementation ("com.nineoldandroids:library:2.4.0")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //lottie animation
    implementation("com.airbnb.android:lottie:6.2.0")

    //gif
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.28")

    //color palette
    //implementation("com.github.duanhong169:colorpicker:1.1.6")
    implementation("com.github.yukuku:ambilwarna:2.0.1")

    implementation("com.github.bumptech.glide:glide:4.14.2")
}