plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val vertx = "5.0.1"
    implementation("io.vertx:vertx-web:$vertx")
    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("com.h2database:h2:2.3.232")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.1")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.18")

    testImplementation("io.vertx:vertx-junit5:$vertx")
    testImplementation("io.vertx:vertx-web-client:$vertx")
    testImplementation("org.mockito:mockito-core:5.18.0")
}

tasks.test {
    useJUnitPlatform()
}