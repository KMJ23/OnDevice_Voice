plugins {
    id("example.android.library")
    id("example.android.hilt")
    id("example.android.library.compose")
}

android {
    namespace = "com.example.core.designsystem"

    lint {
        checkDependencies = true
    }
}

dependencies {
    //lintPublish(project(":lint"))
    implementation(libs.kotlinx.coroutines.android)
}
