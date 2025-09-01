plugins {
    id("example.android.feature")
    id("example.android.hilt")
    id("example.android.library.compose")
}

android {
    namespace = "com.example.core.chat"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
}
