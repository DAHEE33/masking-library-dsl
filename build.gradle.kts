plugins {
    `java-library`
    `maven-publish`
}

group = "com.masking"
version = "0.1.0"

java {
    // Java 8 호환
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    // SLF4J + Jackson + JUnit
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")

    // H2 데이터베이스 (in-memory)
    implementation("com.h2database:h2:2.1.214")
    // 간단한 DataSource 풀 (선택)
    implementation("com.zaxxer:HikariCP:5.0.1")

    // JavaMail (이메일 전송)
    implementation("com.sun.mail:javax.mail:1.6.2")

    testImplementation("com.github.tomakehurst:wiremock-jre8:2.35.0")
    testImplementation("com.icegreen:greenmail:1.6.3")

}

tasks.test {
    useJUnitPlatform()
}
