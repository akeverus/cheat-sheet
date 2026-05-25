plugins {
    `java-library`
}

dependencies {
    api(project(":quiz-domain"))
    implementation(platform(libs.spring.boot.bom))

    implementation(libs.spring.context)
    implementation(libs.spring.jdbc)
    implementation(libs.spring.tx)
    implementation(libs.commons.lang3)
    implementation(libs.slf4j.api)
    compileOnly("jakarta.persistence:jakarta.persistence-api:3.1.0")

    // Flyway: миграции живут в classpath:db/migration в этом модуле и
    // подхватываются и приложением, и тестами.
    implementation(libs.flyway.core)
    runtimeOnly(libs.flyway.database.postgresql)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(platform(libs.spring.boot.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")
    testRuntimeOnly("org.postgresql:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
