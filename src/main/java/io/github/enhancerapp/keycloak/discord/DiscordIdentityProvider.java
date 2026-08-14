package io.github.enhancerapp.keycloak.discord;
import java.io.IOException;
import org.apache.http.client.config.RequestConfig;
import org.jboss.logging.Logger;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.http.simple.SimpleHttp;
import org.keycloak.http.simple.SimpleHttpResponse;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import com.fasterxml.jackson.databind.JsonNode;

public class DiscordIdentityProvider extends AbstractOAuth2IdentityProvider<DiscordIdentityProviderConfig> implements SocialIdentityProvider<DiscordIdentityProviderConfig> {
    static final Logger LOG = Logger.getLogger(DiscordIdentityProvider.class);
    public static final String DEFAULT_SCOPE = "identify email";
    static final String FAILURE = "Could not retrieve the Discord user profile. Check the provider configuration and server log.";
    private static final RequestConfig TIMEOUTS = RequestConfig.custom().setConnectTimeout(5_000).setSocketTimeout(5_000).setConnectionRequestTimeout(5_000).build();
    public DiscordIdentityProvider(KeycloakSession session, DiscordIdentityProviderConfig config) { super(session, config); config.applyDefaults(); if (config.isStoreToken() == null) config.setStoreToken(false); }
    @Override protected String getDefaultScopes() { return DEFAULT_SCOPE; }
    @Override public BrokeredIdentityContext getFederatedIdentity(String response) {
        try { JsonNode token = mapper.readTree(response); JsonNode access = token == null ? null : token.get(OAUTH2_PARAMETER_ACCESS_TOKEN); if (access == null || !access.isTextual() || access.asText().isBlank()) throw new IdentityBrokerException(FAILURE); }
        catch (IdentityBrokerException e) { throw e; } catch (Exception e) { LOG.warn("Discord token response was not valid JSON."); throw new IdentityBrokerException(FAILURE); }
        return super.getFederatedIdentity(response);
    }
    protected SimpleHttp createHttp() { return SimpleHttp.create(session).withRequestConfig(TIMEOUTS).withMaxConsumedResponseSize(256L * 1024L); }
    @Override protected BrokeredIdentityContext doGetFederatedIdentity(String token) {
        if (token == null || token.isBlank()) throw new IdentityBrokerException(FAILURE);
        try (SimpleHttpResponse response = createHttp().doGet(getConfig().getUserInfoUrl()).header("Authorization", "Bearer " + token).header("Accept", "application/json").asResponse()) {
            if (response.getStatus() < 200 || response.getStatus() > 299) { LOG.warnf("Discord profile endpoint returned HTTP %d.", response.getStatus()); throw new IdentityBrokerException(FAILURE); }
            return extractIdentityFromProfile(null, response.asJson());
        } catch (IdentityBrokerException e) { throw e; } catch (IOException | RuntimeException e) { LOG.warnf("Discord profile endpoint failed (%s).", e.getClass().getSimpleName()); throw new IdentityBrokerException(FAILURE); }
    }
    @Override protected BrokeredIdentityContext extractIdentityFromProfile(EventBuilder event, JsonNode json) {
        final DiscordProfile profile;
        try { profile = DiscordProfileParser.parse(json); } catch (DiscordProfileParser.DiscordProfileException e) { LOG.warnf("Rejected Discord profile: %s", e.getMessage()); throw new IdentityBrokerException(e.getMessage()); }
        BrokeredIdentityContext user = new BrokeredIdentityContext(profile.id(), getConfig());
        user.setUsername(getConfig().getUsernameStrategy().resolve(profile, getConfig().getUsernamePrefix())); user.setModelUsername(user.getUsername()); user.setEmail(profile.email()); user.setUserAttribute("discord.id", profile.id()); user.setUserAttribute("discord.login", profile.login()); setOptional(user, "discord.display_name", profile.displayName()); setOptional(user, "discord.email", profile.email()); setOptional(user, "discord.profile_image_url", profile.profileImageUrl()); user.setIdp(this); AbstractJsonUserAttributeMapper.storeUserProfileForMapper(user, json, getConfig().getAlias()); return user;
    }
    private static void setOptional(BrokeredIdentityContext context, String name, String value) { if (value != null) context.setUserAttribute(name, value); }
    @Override public void importNewUser(KeycloakSession session, RealmModel realm, UserModel user, BrokeredIdentityContext context) { sync(user, context); }
    @Override public void updateBrokeredUser(KeycloakSession session, RealmModel realm, UserModel user, BrokeredIdentityContext context) { sync(user, context); }
    private static void sync(UserModel user, BrokeredIdentityContext context) { user.setEmail(context.getEmail()); for (String key : new String[]{"discord.id", "discord.login", "discord.display_name", "discord.email", "discord.profile_image_url"}) { String value = context.getUserAttribute(key); if (value == null || value.isBlank()) user.removeAttribute(key); else user.setSingleAttribute(key, value); } }
}
