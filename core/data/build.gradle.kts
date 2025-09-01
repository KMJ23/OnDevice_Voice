plugins {
    id("example.android.library")
    id("example.android.hilt")
}

android {
    namespace = "com.example.core.data"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
}
