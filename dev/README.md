# Development Container

## Pulsar Manager setup
After first startup of Pulsar Manager container for development you need to set up a super-user and an environment.  
Run the following commands in the command line to create a super-user with username `admin` and password `adminadmin`:
```bash
CSRF_TOKEN=$(curl http://localhost:7750/pulsar-manager/csrf-token)
curl -H 'X-XSRF-TOKEN: $CSRF_TOKEN' -H 'Cookie: XSRF-TOKEN=$CSRF_TOKEN;' -H "Content-Type: application/json" -X PUT http://localhost:7750/pulsar-manager/users/superuser -d '{"name": "admin", "password": "adminadmin", "description": "admin", "email": "admin@test.org"}'
```

After creating the super-user, open the Pulsar Manager UI in your browser (http://localhost:9527/) and log in with the super-user credentials. Then create a new environment
and fill the form with the following values:
- Environment Name: `standalone`
- Service URL: `http://pulsar-standalone:8080`
- Bookie URL: `http://pulsar-standalone:3181`