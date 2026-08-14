package io.github.enhancerapp.keycloak.discord;
import static org.assertj.core.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
class DiscordProfileParserTest {
    private final ObjectMapper json = new ObjectMapper();
    @Test void mapsDiscordProfileAndCustomAvatar() throws Exception { DiscordProfile p = DiscordProfileParser.parse(json.readTree("{\"id\":\"175928847299117063\",\"username\":\"discord\",\"global_name\":\"Discord\",\"email\":\"a@example.test\",\"avatar\":\"hash\"}")); assertThat(p.id()).isEqualTo("175928847299117063"); assertThat(p.displayName()).isEqualTo("Discord"); assertThat(p.profileImageUrl()).isEqualTo("https://cdn.discordapp.com/avatars/175928847299117063/hash.png?size=256"); }
    @Test void fallsBackToUsernameAndDefaultAvatar() throws Exception { DiscordProfile p = DiscordProfileParser.parse(json.readTree("{\"id\":\"175928847299117063\",\"username\":\"discord\",\"avatar\":null}")); assertThat(p.displayName()).isEqualTo("discord"); assertThat(p.profileImageUrl()).startsWith("https://cdn.discordapp.com/embed/avatars/"); }
    @Test void rejectsMissingId() throws Exception { assertThatThrownBy(() -> DiscordProfileParser.parse(json.readTree("{\"username\":\"discord\"}"))).hasMessageContaining("stable account ID"); }
    @Test void rejectsMissingUsername() throws Exception { assertThatThrownBy(() -> DiscordProfileParser.parse(json.readTree("{\"id\":\"1\"}"))).hasMessageContaining("username"); }
}
