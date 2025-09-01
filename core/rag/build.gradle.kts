plugins {
    id("example.android.library")
    id("example.android.hilt")
}

android {
    namespace = "com.example.core.rag"

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":library:llama.cpp:llama"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.langchain4j)
    implementation(libs.langchain4j.document.parser.apache.poi)
    implementation(libs.langgraph4j.core)
    implementation(libs.langgraph4j.core.jdk8)

    implementation(libs.microsoft.onnxruntime.android)
    implementation(libs.kotlin.deeplearning.onnx)
    implementation(libs.onnxruntime.extensions.android)

    // github shubham0204/Sentence-Embeddings-Android
    implementation(libs.sentence.embeddings.android)

    implementation(libs.itextpdf)
}
