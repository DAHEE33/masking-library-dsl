plugins {
    `java-library`
    `maven-publish`
    id("org.owasp.dependencycheck") version "8.4.0"
}

group = "com.masking"
version = "0.1.0"

java {
    // Java 8 호환
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
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
    implementation("com.icegreen:greenmail:1.6.3")
    testImplementation("com.icegreen:greenmail:1.6.3")

    testImplementation("com.github.tomakehurst:wiremock-jre8:2.35.0")

}

tasks.test {
    useJUnitPlatform()
}

// OWASP Dependency Check 설정
dependencyCheck {
    failBuildOnCVSS = 7.0f
    formats = listOf("HTML", "JSON")
    suppressionFile = "config/dependency-check-suppressions.xml"
}

// Javadoc 설정
tasks.javadoc {
    options {
        (this as StandardJavadocDocletOptions).apply {
            encoding = "UTF-8"
            charSet = "UTF-8"
            docEncoding = "UTF-8"
            author()
            version()
            title = "Masking Library API Documentation"
            windowTitle = "Masking Library API"
            docTitle = "<h1>Masking Library API Documentation</h1>"
            bottom = "Copyright &copy; 2024"
            links("https://docs.oracle.com/javase/8/docs/api/")
        }
    }
}

// Javadoc JAR 생성
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
}

// Sources JAR 생성
val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

// Maven Publish 설정
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            
            artifact(javadocJar)
            artifact(sourcesJar)
            
            pom {
                name.set("Masking Library")
                description.set("Action-based data protection library for masking, tokenization, encryption, and auditing")
                url.set("https://github.com/yourusername/masking-library")
                
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                
                developers {
                    developer {
                        id.set("yourusername")
                        name.set("Your Name")
                        email.set("your.email@example.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/yourusername/masking-library.git")
                    developerConnection.set("scm:git:ssh://github.com:yourusername/masking-library.git")
                    url.set("https://github.com/yourusername/masking-library")
                }
            }
        }
    }
}
