/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin library project to get you started.
 */

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.4.31"

    // Apply the java-library plugin for API and implementation separation.
    id("java-library")

    id("signing")
    id("maven-publish")
}

java {
    withJavadocJar()
    withSourcesJar()
}


group = "com.vonage"
version = "0.1.0"

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.springframework:spring-web:5.3.4")
    implementation("org.springframework:spring-webmvc:6.0.8")
    implementation("javax.servlet:javax.servlet-api:3.0.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.2")
    implementation("com.vonage:client:6.2.0")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.springframework:spring-test:5.3.5")
    testImplementation("org.apache.httpcomponents:httpcore:4.4.11")
}


publishing{
    publications {
        create<MavenPublication>("mavenJava"){
            artifactId = "spring"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom{
                name.set("spring")
                description.set("A Vonage middleware library for spring")
                artifactId = "spring"
                url.set("https://github.com/nexmo-community/vonage-java-spring")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                issueManagement{
                    system.set("GitHub")
                    url.set("https://github.com/nexmo-comunity/vonage-java-spring/issues")
                }
                developers{
                    developer{
                        id.set("devrel")
                        name.set("Voange Developer Relations Team")
                        email.set("devrel@vonage.com")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:nexmo-community/vonage-java-spring")
                    developerConnection.set("scm:git:git@github.com:nexmo-community/vonage-java-spring")
                    url.set("http://github.com/nexmo-community/vonage-java-spring")
                }
                organization{
                    name.set("Vonage")
                    url.set("https://developer.nexmo.com")
                }
            }
        }
    }
    repositories{
        maven{
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            credentials.username = System.getenv("OSS_USERNAME")
            credentials.password = System.getenv("OSS_PASSWORD")
            url = if(version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}

signing {
    val signingKey: String? = System.getenv("signingKey")
    val signingPassword: String? = System.getenv("signingPassword")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

