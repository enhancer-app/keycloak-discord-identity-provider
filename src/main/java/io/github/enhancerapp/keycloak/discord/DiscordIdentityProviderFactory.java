package io.github.enhancerapp.keycloak.discord;
import java.util.List;
import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
public class DiscordIdentityProviderFactory extends AbstractIdentityProviderFactory<DiscordIdentityProvider> implements SocialIdentityProviderFactory<DiscordIdentityProvider> {
    public static final String PROVIDER_ID = "discord";
    @Override public String getName() { return "Discord"; }
    @Override public String getId() { return PROVIDER_ID; }
    @Override public DiscordIdentityProvider create(KeycloakSession session, IdentityProviderModel model) { return new DiscordIdentityProvider(session, new DiscordIdentityProviderConfig(model)); }
    @Override public DiscordIdentityProviderConfig createConfig() { DiscordIdentityProviderConfig config = new DiscordIdentityProviderConfig(); config.applyDefaults(); return config; }
    @Override public List<ProviderConfigProperty> getConfigProperties() { return ProviderConfigurationBuilder.create()
        .property().name("authorizationUrl").label("Authorization URL").type(ProviderConfigProperty.STRING_TYPE).defaultValue(DiscordEndpoints.AUTHORIZATION_URL).add()
        .property().name("tokenUrl").label("Token URL").type(ProviderConfigProperty.STRING_TYPE).defaultValue(DiscordEndpoints.TOKEN_URL).add()
        .property().name("userInfoUrl").label("Profile URL").type(ProviderConfigProperty.STRING_TYPE).defaultValue(DiscordEndpoints.PROFILE_URL).add()
        .property().name(DiscordIdentityProviderConfig.USERNAME_STRATEGY).label("Username strategy").type(ProviderConfigProperty.LIST_TYPE).options("stable-id-prefix", "provider-username").defaultValue("stable-id-prefix").add()
        .property().name(DiscordIdentityProviderConfig.USERNAME_PREFIX).label("Username prefix").type(ProviderConfigProperty.STRING_TYPE).defaultValue("discord-").add().build(); }
}
