plugins {
    id("example.android.library")
    id("example.android.hilt")
}

android {
    namespace = "com.example.core.datastore"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
}
