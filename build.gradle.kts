plugins {
    id("java")
    id("maven-publish")
}

group = "dev.lightdream"
version = "4.0.0"

repositories {
    mavenCentral()
    maven("https://repo.lightdream.dev/")
}

dependencies {
    // LightDream
    implementation("dev.lightdream:logger:3.2.0")
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        val githubURL = project.findProperty("github.url") ?: ""
        val githubUsername = project.findProperty("github.auth.username") ?: ""
        val githubPassword = project.findProperty("github.auth.password") ?: ""

        val selfURL = project.findProperty("self.url") ?: ""
        val selfUsername = project.findProperty("self.auth.username") ?: ""
        val selfPassword = project.findProperty("self.auth.password") ?: ""

        maven(url = githubURL as String) {
            name = "github"
            credentials(PasswordCredentials::class) {
                username = githubUsername as String
                password = githubPassword as String
            }
        }

        maven(url = selfURL as String) {
            name = "self"
            credentials(PasswordCredentials::class) {
                username = selfUsername as String
                password = selfPassword as String
            }
        }
    }
}

tasks.register("publishGitHub") {
    dependsOn("publishMavenPublicationToGithubRepository")
    description = "Publishes to GitHub"
}

tasks.register("publishSelf") {
    dependsOn("publishMavenPublicationToSelfRepository")
    description = "Publishes to Self hosted repository"
}

