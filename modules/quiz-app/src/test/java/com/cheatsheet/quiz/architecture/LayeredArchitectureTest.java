package com.cheatsheet.quiz.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.importer.ImportOption;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Архитектурные guardrails: тесты фиксируют границы слоев и предотвращают
 * "протекание" зависимостей между API, service, persistence и domain.
 */
@AnalyzeClasses(
        packages = "com.cheatsheet.quiz",
        importOptions = {ImportOption.DoNotIncludeTests.class}
)
class LayeredArchitectureTest {

    @ArchTest
    static final ArchRule domainIsIndependent = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "..api..",
                    "..service..",
                    "..persistence..",
                    "..config.."
            )
            .because("доменный слой должен оставаться независимым от инфраструктуры и транспорта");

    @ArchTest
    static final ArchRule serviceDoesNotDependOnApi = noClasses()
            .that().resideInAPackage("..service..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..api.controller..", "..api.security..", "..api.exception..")
            .because("service-слой не должен зависеть от web/API и контроллеров; зависимость от api.dto (response DTO) допускается");

    @ArchTest
    static final ArchRule persistenceDoesNotDependOnApi = noClasses()
            .that().resideInAPackage("..persistence..")
            .should().dependOnClassesThat()
            .resideInAPackage("..api..")
            .because("persistence-слой не должен знать о transport/API деталях");
}
