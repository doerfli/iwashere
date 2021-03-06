import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version Versions.spring
	id("io.spring.dependency-management") version Versions.springDependencyManagement
	kotlin("jvm") version Versions.kotlin
	kotlin("plugin.spring") version Versions.kotlin
	kotlin("plugin.noarg") version Versions.kotlin
	kotlin("plugin.allopen") version Versions.kotlin
	id("com.github.ben-manes.versions") version Versions.benManesVersions
	id("nebula.release") version Versions.nebulaRelease
}

group = "li.doerf"
java.sourceCompatibility = JavaVersion.VERSION_11

//val developmentOnly by configurations.creating
configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	maven { url = uri("https://repo.spring.io/release") }
	mavenCentral()
	jcenter()
}


dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${Versions.springCloud}")
	}
}

dependencies {
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${Versions.coroutinesVersion}")
	implementation("org.thymeleaf:thymeleaf-spring5")
	implementation("org.springdoc:springdoc-openapi-ui:${Versions.springDocOpenApi}")
	implementation("org.springframework.cloud:spring-cloud-stream")
	implementation("org.springframework.cloud:spring-cloud-stream-binder-rabbit")

	implementation("com.github.kittinunf.fuel:fuel:${Versions.fuel}")
	implementation("com.github.kittinunf.fuel:fuel-coroutines:${Versions.fuel}")
	implementation("io.github.cdimascio:java-dotenv:${Versions.javaDotenv}")
	implementation("com.opencsv:opencsv:${Versions.openCSV}")
	implementation("org.apache.poi:poi:${Versions.apachePOI}")
	implementation("org.apache.poi:poi-ooxml:${Versions.apachePOI}")
	implementation("com.github.javafaker:javafaker:${Versions.javaFaker}")

	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.flywaydb:flyway-core")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
		exclude(module = "mockito-core")
	}
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("com.ninja-squad:springmockk:${Versions.mockk}")
	testImplementation("io.ktor:ktor-client-apache:${Versions.ktor}")
//	testImplementation("org.springframework.amqp:spring-rabbit-test")
//	testImplementation("org.springframework.cloud:spring-cloud-stream-test-support")
//	testImplementation("org.springframework.integration:spring-integration-test")

	testRuntimeOnly("com.h2database:h2")
}

springBoot {
	buildInfo()
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

configure<org.jetbrains.kotlin.noarg.gradle.NoArgExtension> {
	annotation("li.doerf.iwashere.utils.NoArgs")
	invokeInitializers = true
}