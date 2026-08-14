package io.github.enhancerapp.keycloak.discord;
import java.util.Locale;
public enum DiscordUsernameStrategy {
    STABLE_ID_PREFIX("stable-id-prefix"), PROVIDER_USERNAME("provider-username");
    public static final String DEFAULT_PREFIX = "discord-";
    private final String value;
    DiscordUsernameStrategy(String value) { this.value = value; }
    public String value() { return value; }
    public static DiscordUsernameStrategy from(String value) {
        if (value != null) for (DiscordUsernameStrategy strategy : values()) if (strategy.value.equals(value.trim().toLowerCase(Locale.ROOT))) return strategy;
        return STABLE_ID_PREFIX;
    }
    public String resolve(DiscordProfile profile, String prefix) {
        if (this == PROVIDER_USERNAME && profile.login() != null && !profile.login().isBlank()) return profile.login();
        return (prefix == null || prefix.isBlank() ? DEFAULT_PREFIX : prefix.trim()) + profile.id();
    }
}
