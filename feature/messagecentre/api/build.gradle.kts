plugins { 
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}
android {
    compileSdk = 35
    namespace = "com.example.feature.messagecentre.api"

    defaultConfig {
        minSdk = 26
        targetSdk = 35
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
dependencies { }
