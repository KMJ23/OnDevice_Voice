plugins {
    id("example.android.library")
    id("example.android.hilt")
    id("example.android.library.compose")
}

android {
    namespace = "com.example.core.ui"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(libs.kotlinx.coroutines.android)
}
