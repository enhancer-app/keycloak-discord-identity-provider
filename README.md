# Keycloak Discord Identity Provider

MIT-licensed, generic Keycloak social identity provider for Discord OAuth2. It registers **Discord** in **Identity Providers → Add provider** and uses the stable Discord user `id` as the only federated identity key.

> Discord is a trademark of its respective owner. This project is independent and not endorsed by Discord.

## Build

```bash
./gradlew clean build
```

Deployable release JAR: `keycloak-discord-identity-provider-<version>.jar`.

## Install

```bash
cp keycloak-discord-identity-provider-<version>.jar /opt/keycloak/providers/keycloak-discord.jar
/opt/keycloak/bin/kc.sh build
/opt/keycloak/bin/kc.sh start --optimized
```

For Docker builds:

```bash
./gradlew prepareDockerProvider
docker build -t keycloak-discord:local .
```

## Configure Discord

Create a Discord application and register:

```text
https://<keycloak-host>/realms/<realm>/broker/<alias>/endpoint
```

Configure Client ID and Client Secret in Keycloak. Defaults are `identify email` scope and:

```text
Authorization: https://discord.com/oauth2/authorize
Token:         https://discord.com/api/v10/oauth2/token
Profile:       https://discord.com/api/v10/users/@me
```

The endpoint defaults are editable by realm administrators. Keycloak PKCE configuration is supported.

## Profile mapping

Every login refreshes Keycloak email when supplied and these namespaced attributes:

| Discord field | Keycloak value |
| --- | --- |
| `id` | federated identity ID, `discord.id` |
| `username` | `discord.login` |
| `global_name` | `discord.display_name`, falling back to username |
| `email` | Keycloak email and `discord.email` |
| `avatar` | `discord.profile_image_url`, built from Discord CDN or default-avatar URL |

Default username is stable `discord-{id}`. Administrators can select `provider-username` or change the prefix; neither changes the federated identity key.

## Security

The provider rejects malformed profiles, missing IDs/usernames and non-2xx API responses. HTTP calls use bounded timeouts. It never logs authorization codes, tokens, secrets or raw Discord responses. The provider JAR bundles no Keycloak, Jackson, HTTP, or logging dependency.

## License

MIT. See [LICENSE](LICENSE).
