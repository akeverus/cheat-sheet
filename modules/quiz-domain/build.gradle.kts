plugins {
    `java-library`
}

dependencies {
    implementation(libs.slf4j.api)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(platform(libs.spring.boot.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
