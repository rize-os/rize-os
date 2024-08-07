# Access-Manager

## Configuration
The configuration for the Access-Manager can be provided as .yaml or .properties file. The following tables lists all parameters that can be configured.

### Basic Configuration
| Parameter               | Description                                                         | Default Value |
|-------------------------|---------------------------------------------------------------------|---------------|
| `access-manager.port`   | The port on which the Access-Manager listens for incoming requests. | `9090`        |

### Keycloak Configuration
| Parameter                      | Description                                                                                                                     | Default Value           |
|--------------------------------|---------------------------------------------------------------------------------------------------------------------------------|-------------------------|
| `keycloak.url`                 | URL of the Keycloak server.                                                                                                     | `http://localhost:8080` |
| `keycloak.realm`               | Main realm for storing data like organizations, users and clients.                                                              | `master`                |
| `keycloak.admin.realm`         | Admin realm that is used for the API communication to Keycloak                                                                  | `master`                |
| `keycloak.admin.grant-type`    | The grant type for authentication to Keycloak [`password`; `client_credentials`]                                                | `password`              |
| `keycloak.admin.client-id`     | Client-ID that is used for the API communication to Keycloak                                                                    | `admin-cli`             |
| `keycloak.admin.client-secret` | Client-Secret for authentication to Keycloak. Is only required when `keycloak.admin.grant-type` is set to `client_credentials`. | -                       |
| `keycloak.admin.username`      | Username for authentication to Keycloak. Is only required when `keycloak.admin.grant-type` is set to `password`.                | `admin`                 |
| `keycloak.admin.password`      | Password for authentication to Keycloak. Is only required when `keycloak.admin.grant-type` is set to `password`.                | -                       |
| `datasource.url`               | URL of the database that is used for storing additional data for the Access Manager                                             | -                       |
| `datasource.username`          | Username for the database that is used for storing additional data for the Access Manager                                       | -                       |
| `datasource.password`          | Password for the database that is used for storing additional data for the Access Manager                                       | -                       |
