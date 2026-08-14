package io.github.enhancerapp.keycloak.discord;
import java.math.BigInteger;
import com.fasterxml.jackson.databind.JsonNode;
public final class DiscordProfileParser {
    private DiscordProfileParser() { }
    public static DiscordProfile parse(JsonNode node) {
        if (node == null || !node.isObject()) throw new DiscordProfileException("Discord profile response was not a JSON object.");
        String id = text(node, "id");
        if (id == null) throw new DiscordProfileException("Discord profile did not contain a stable account ID.");
        String login = text(node, "username");
        if (login == null) throw new DiscordProfileException("Discord profile did not contain a username.");
        String display = text(node, "global_name");
        if (display == null) display = login;
        return new DiscordProfile(id, login, display, text(node, "email"), avatar(id, text(node, "avatar")));
    }
    private static String text(JsonNode node, String key) { JsonNode value = node.get(key); return value != null && value.isTextual() && !value.asText().isBlank() ? value.asText().trim() : null; }
    private static String avatar(String id, String hash) {
        if (hash != null) return "https://cdn.discordapp.com/avatars/" + id + "/" + hash + ".png?size=256";
        try { return "https://cdn.discordapp.com/embed/avatars/" + new BigInteger(id).shiftRight(22).mod(BigInteger.valueOf(6)) + ".png"; }
        catch (NumberFormatException e) { return null; }
    }
    public static final class DiscordProfileException extends RuntimeException { private static final long serialVersionUID = 1L; DiscordProfileException(String message) { super(message); } }
}
