# Compatibility

| Provider release line | Java | Guaranteed Keycloak version | Tested image |
| --- | --- | --- | --- |
| `1.x` | 17 | `26.5.2` | `quay.io/keycloak/keycloak:26.5.2` |

The provider uses Keycloak provider SPI APIs and `org.keycloak.http.simple`. Newer Keycloak
versions may work, but are not a compatibility claim until provider discovery and real Discord
login testing pass. No reflection shims are used for binary API changes.
