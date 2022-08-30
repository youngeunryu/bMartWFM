plugins {
    val kotlinVersion = "1.6.21"
    val flywayPluginVersion = "9.1.3"
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.0.13.RELEASE"
    id("org.flywaydb.flyway") version flywayPluginVersion
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
}

group = "bMartWFM"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

extra.apply {
    this["querydsl.version"] = "5.0.0"
    this["mapstruct.version"] = "1.5.2.Final"
    this["poi.version"] = "5.2.2"
    this["kotlinLogging.version"] = "2.1.23"
    this["logtash.version"] = "7.2"
    this["kotest.version"] = "5.4.1"
    this["kotest.spring.version"] = "1.1.2"
    this["kotest.wiremock.version"] = "1.0.3"
    this["mockk.version"] = "1.12.5"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.data:spring-data-envers")
    implementation("com.querydsl:querydsl-jpa:${property("querydsl.version")}")
    kapt("com.querydsl:querydsl-apt:${property("querydsl.version")}:jpa")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    implementation("org.mapstruct:mapstruct:${property("mapstruct.version")}")
    kapt("org.mapstruct:mapstruct-processor:${property("mapstruct.version")}")

    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("io.github.microutils:kotlin-logging:${property("kotlinLogging.version")}")
    implementation("net.logstash.logback:logstash-logback-encoder:7.2")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    implementation("org.apache.poi:poi-ooxml:${property("poi.version")}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5:${property("kotest.version")}")
    testImplementation("io.kotest:kotest-assertions-core:${property("kotest.version")}")
    testImplementation("io.kotest:kotest-framework-datatest:${property("kotest.version")}")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:${property("kotest.spring.version")}")
    testImplementation("io.kotest.extensions:kotest-extensions-wiremock:${property("kotest.wiremock.version")}")
    testImplementation("io.mockk:mockk:${property("mockk.version")}")
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }
    test {
        useJUnitPlatform()
    }
    ktlint {
        verbose.set(true)
    }
}
