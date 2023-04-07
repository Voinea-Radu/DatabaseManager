plugins {
    id("java")
}

group = "dev.lightdream"
version = "3.9.2"

repositories {
    mavenCentral()
    maven("https://repo.lightdream.dev/")
}

dependencies {
    // LightDream
    implementation("dev.lightdream:logger:3.1.0")
    implementation("dev.lightdream:lambda:4.0.0")

    // Gson
    implementation("com.google.code.gson:gson:2.8.9")

    // Database
    implementation("com.zaxxer:HikariCP:4.0.3")

    // Driver - SQLite
    implementation("org.xerial:sqlite-jdbc:3.41.2.1")

    // Driver - MYSQL
    implementation("mysql:mysql-connector-java:8.0.32")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    // Jetbrains
    compileOnly("org.jetbrains:annotations:23.1.0")
    annotationProcessor("org.jetbrains:annotations:23.1.0")

    // Reflections
    implementation("org.reflections:reflections:0.10.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}