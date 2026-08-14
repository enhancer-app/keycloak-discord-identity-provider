package io.github.enhancerapp.keycloak.discord;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.models.IdentityProviderModel;
public class DiscordIdentityProviderConfig extends OAuth2IdentityProviderConfig {
    private static final long serialVersionUID = 1L;
    public static final String USERNAME_STRATEGY = "usernameStrategy";
    public static final String USERNAME_PREFIX = "usernamePrefix";
    public DiscordIdentityProviderConfig() { }
    public DiscordIdentityProviderConfig(IdentityProviderModel model) { super(model); }
    public void applyDefaults() {
        if (getAuthorizationUrl() == null || getAuthorizationUrl().isBlank()) setAuthorizationUrl(DiscordEndpoints.AUTHORIZATION_URL);
        if (getTokenUrl() == null || getTokenUrl().isBlank()) setTokenUrl(DiscordEndpoints.TOKEN_URL);
        if (getUserInfoUrl() == null || getUserInfoUrl().isBlank()) setUserInfoUrl(DiscordEndpoints.PROFILE_URL);
        if (getDefaultScope() == null || getDefaultScope().isBlank()) setDefaultScope(DiscordIdentityProvider.DEFAULT_SCOPE);
    }
    public DiscordUsernameStrategy getUsernameStrategy() { return DiscordUsernameStrategy.from(getConfig().get(USERNAME_STRATEGY)); }
    public String getUsernamePrefix() { String value = getConfig().get(USERNAME_PREFIX); return value == null || value.isBlank() ? DiscordUsernameStrategy.DEFAULT_PREFIX : value.trim(); }
}
