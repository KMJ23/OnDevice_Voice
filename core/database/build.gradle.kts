plugins {
    id("example.android.library")
    id("example.android.hilt")
    id("io.objectbox")
}

android {
    namespace = "com.example.core.database"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.objectbox.gradle.plugin)
    releaseImplementation(libs.objectbox.android)

    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
}