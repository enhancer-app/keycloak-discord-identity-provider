# Manual End-to-End Test

Use a non-production Keycloak realm and dedicated Discord application. Do not commit client
credentials, authorization codes, tokens, or real profile data.

1. Build the JAR and install it in `/opt/keycloak/providers/keycloak-discord.jar`.
2. Run `kc.sh build` for optimized Keycloak, then restart it.
3. Add **Discord** in the target realm under **Identity Providers**.
4. Register this exact callback URI in Discord Developer Portal:

```text
https://<keycloak-host>/realms/<realm>/broker/<alias>/endpoint
```

5. Enter the Discord Client ID and Client Secret in Keycloak; retain `identify email` scope.
6. Login through Discord, then inspect the provider link (`id`) and refreshed attributes
   `discord.id`, `discord.login`, `discord.display_name`, `discord.email`, and
   `discord.profile_image_url`.
