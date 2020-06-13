import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version Versions.spring
	id("io.spring.dependency-management") version Versions.springDependencyManagement
	kotlin("jvm") version Versions.kotlin
	kotlin("plugin.spring") version Versions.kotlin
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

dependencies {
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${Versions.coroutinesVersion}")
	implementation("org.thymeleaf:thymeleaf-spring5")

	implementation("com.github.kittinunf.fuel:fuel:${Versions.fuel}")
	implementation("com.github.kittinunf.fuel:fuel-coroutines:${Versions.fuel}")
	implementation("io.github.cdimascio:java-dotenv:${Versions.javaDotenv}")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
		exclude(module = "mockito-core")
	}
//	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("com.ninja-squad:springmockk:${Versions.mockk}")
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
