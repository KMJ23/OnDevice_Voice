plugins {
    id("example.android.feature")
    id("example.android.hilt")
    id("example.android.library.compose")
}

android {
    namespace = "com.example.core.document"
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
}
