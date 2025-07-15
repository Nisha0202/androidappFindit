plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.lostandfound"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nisha0202.lostandfound"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "1.3"

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
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    //storage and database
    implementation("com.google.firebase:firebase-storage:21.0.2")
    implementation ("com.google.firebase:firebase-database:21.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //image circle
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation ("com.firebaseui:firebase-ui-auth:7.2.0")
    implementation ("com.google.android.gms:play-services-auth-api-phone:18.2.0")
    implementation ("com.google.android.gms:play-services-identity:18.1.0")

    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    // Firebase Authentication dependency
    implementation ("com.google.firebase:firebase-auth:23.2.1")
}

