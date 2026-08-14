# Security Policy

## Reporting a vulnerability

Do not open a public issue for a suspected vulnerability. Report it privately through GitHub
Security Advisories: <https://github.com/enhancer-app/keycloak-discord-identity-provider/security/advisories/new>.

Never include client secrets, authorization codes, access tokens, refresh tokens, or real Discord
profile data in a report.

## Security constraints

- Federated identity uses only Discord `id`.
- Provider profile attributes and email are refreshed on every login, but never establish identity.
- HTTP calls have bounded timeouts and a response-size limit.
- Token/profile failures use payload-safe errors. No provider log contains codes, secrets, tokens,
  or raw remote responses.
- The JAR bundles no Keycloak, Jackson, HTTP, or logging library.
