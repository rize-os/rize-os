# Platform

## Configuration
The configuration for the platform can be provided as .yaml or .properties file. The following tables lists all parameters that can be configured.

### Keycloak Configuration
| Parameter                      | Description                                                                                                                                    | Data Type | Default Value           |
|--------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|-----------|-------------------------|
| `keycloak.url`                 | URL of the Keycloak server                                                                                                                     | String    | `http://localhost:8088` |
| `keycloak.realm`               | Main realm for storing data like organizations, users and clients                                                                              | String    | `master`                |
| `keycloak.admin.realm`         | Admin realm that is used for the API communication to Keycloak                                                                                 | String    | `master`                |
| `keycloak.admin.grant-type`    | The grant type for authentication to Keycloak [`password`; `client_credentials`]                                                               | String    | `password`              |
| `keycloak.admin.client-id`     | Client-ID that is used for the API communication to Keycloak. Is only required when `keycloak.admin.grant-type` is set to `client_credentials` | String    | `admin-cli`             |
| `keycloak.admin.client-secret` | Client-Secret for authentication to Keycloak. Is only required when `keycloak.admin.grant-type` is set to `client_credentials`                 | String    | -                       |
| `keycloak.admin.username`      | Username for authentication to Keycloak. Is only required when `keycloak.admin.grant-type` is set to `password`                                | String    | `admin`                 |
| `keycloak.admin.password`      | Password for authentication to Keycloak. Is only required when `keycloak.admin.grant-type` is set to `password`                                | String    | -                       |

### Database Configuration
| Parameter           | Description                                        | Data Type | Default Value |
|---------------------|----------------------------------------------------|-----------|---------------|
| `database.url`      | URL of the database server                         | String    | -             |
| `database.username` | Username for authentication to the database server | String    | -             |
| `database.password` | Password for authentication to the database server | String    | -             |