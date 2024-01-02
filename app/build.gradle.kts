plugins {
    id("com.android.application")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.moneymanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.moneymanager"
        minSdk = 24
        targetSdk = 34
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.firebaseui:firebase-ui-database:8.0.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")


    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    implementation("io.reactivex:rxandroid:1.1.0") // RxAndroid
    implementation("io.reactivex:rxjava:1.1.0") // 推荐同时加载RxJava

    implementation("com.squareup.retrofit:retrofit:2.0.0-beta2") // Retrofit网络处理
    implementation("com.squareup.retrofit:adapter-rxjava:2.0.0-beta2") // Retrofit的rx解析库
    implementation("com.squareup.retrofit:converter-gson:2.0.0-beta2") // Retrofit的gson库

}
