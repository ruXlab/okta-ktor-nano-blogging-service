val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val okta_jwt_verifier_version: String by project

plugins {
    application
    kotlin("jvm") version "1.4.0"
}

group = "com.okta.demo.ktor"
version = "0.0.1"

application {
    mainClassName = "io.ktor.server.cio.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-cio:$ktor_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-html-builder:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-server-sessions:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("com.okta.jwt:okta-jwt-verifier:$okta_jwt_verifier_version")
    implementation("com.okta.jwt:okta-jwt-verifier-impl:$okta_jwt_verifier_version")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")
