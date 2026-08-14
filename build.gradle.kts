plugins { `java-library`; `maven-publish` }

group = "io.github.enhancer-app"
version = providers.gradleProperty("version").getOrElse("1.0.0-SNAPSHOT")

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }; withSourcesJar(); withJavadocJar() }

val keycloakVersion = "26.5.2"
dependencies {
    listOf("keycloak-core", "keycloak-common", "keycloak-server-spi", "keycloak-server-spi-private", "keycloak-services").forEach {
        compileOnly("org.keycloak:$it:$keycloakVersion")
        testImplementation("org.keycloak:$it:$keycloakVersion")
    }
    compileOnly("jakarta.ws.rs:jakarta.ws.rs-api:4.0.0")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.22.1")
    compileOnly("org.jboss.logging:jboss-logging:3.6.3.Final")
    compileOnly("org.apache.httpcomponents:httpclient:4.5.14")
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.jboss.resteasy:resteasy-core:6.2.12.Final")
    testImplementation("org.testcontainers:testcontainers:2.0.5")
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.17")
}

tasks.withType<JavaCompile>().configureEach { options.encoding = "UTF-8"; options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing")) }
tasks.jar { archiveBaseName.set("keycloak-discord-identity-provider"); manifest.attributes("Implementation-Title" to "keycloak-discord-identity-provider", "Implementation-Version" to project.version, "Keycloak-Target-Version" to keycloakVersion) }
val prepareDockerProvider = tasks.register<Copy>("prepareDockerProvider") { dependsOn(tasks.jar); from(tasks.jar.flatMap { it.archiveFile }); into(layout.buildDirectory.dir("docker")); rename { "keycloak-discord.jar" } }
val withSmoke = providers.gradleProperty("withSmoke").isPresent
tasks.test {
    useJUnitPlatform { if (!withSmoke) excludeTags("smoke") }
    systemProperty("discord.keycloakVersion", keycloakVersion)
    systemProperty("discord.providerJar", tasks.jar.flatMap { it.archiveFile }.get().asFile.absolutePath)
    systemProperty("org.jboss.logging.provider", "jdk")
}
if (withSmoke) tasks.test { dependsOn(tasks.jar) }
