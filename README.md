
# DatabaseManager

![Build](../../actions/workflows/build.yml/badge.svg)
![Version](https://img.shields.io/badge/Version-5.0.2-red.svg)

# Table Of Contents
1. [Description](#description)
2. [How to add to your project](#how-to-add-to-your-project)
3. [How to use](#how-to-use)

## Description
A database manager lib that is based on Hibernate and makes the setup of it more straight forward. 

## How to add to your project


The artifact can be found at the repository https://repo.lightdream.dev or https://jitpack.io (under com.github.L1ghtDream instead of dev.lightdream)

### Maven
```xml
<repositories>
    <repository>
        <id>lightdream-repo</id>
        <url>https://repo.lightdream.dev/</url>
    </repository>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
```xml
<dependencies>
    <dependency>
        <groupId>dev.lightdream</groupId>
        <artifactId>database-manager</artifactId>
        <version>5.0.2</version>
    </dependency>
    <dependency>
        <groupId>com.github.L1ghtDream</groupId>
        <artifactId>database-manager</artifactId>
        <version>5.0.2</version>
    </dependency>
</dependencies>
```

### Gradle - Groovy DSL
```groovy
repositories {
    maven { url "https://repo.lightdream.dev/" }
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation "dev.lightdream:database-manager:5.0.2"
    implementation "com.github.L1ghtDream:database-manager:5.0.2"
}
```

### Gradle - Kotlin DSL
```kotlin
repositories {
    maven("https://repo.lightdream.dev/")
    maven("https://jitpack.io")
}

dependencies {
    implementation("dev.lightdream:database-manager:5.0.2")
    implementation("com.github.L1ghtDream:database-manager:5.0.2")
}
```

If you want to use an older version that is not available in https://repo.lightdream.dev you can try using https://archive-repo.lightdream.dev


## How to use

Can be found in the [repository](/src/main/java/example)
