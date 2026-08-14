package io.github.enhancerapp.keycloak.discord;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

/** Boots the exact target Keycloak image and confirms the Discord provider loads. */
@Tag("smoke")
class DiscordProviderDiscoverySmokeTest {
    @Test void keycloakLoadsDiscordProvider() {
        Path jar = Path.of(System.getProperty("discord.providerJar"));
        assertThat(jar).exists();
        try (GenericContainer<?> keycloak = new GenericContainer<>("quay.io/keycloak/keycloak:" + System.getProperty("discord.keycloakVersion"))
                .withCopyFileToContainer(MountableFile.forHostPath(jar), "/opt/keycloak/providers/keycloak-discord.jar")
                .withExposedPorts(8080)
                .withCommand("start-dev")
                .waitingFor(Wait.forHttp("/realms/master/.well-known/openid-configuration").forStatusCode(200).withStartupTimeout(Duration.ofMinutes(3)))) {
            keycloak.start();
            assertThat(keycloak.getLogs()).contains("DiscordIdentityProviderFactory");
        }
    }
}
