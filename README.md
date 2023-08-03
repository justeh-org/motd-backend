# Message of the Day :: Backend

> [!NOTE]
> This is the backend component of the
> [Message of the Day](https://github.com/alexandreroman/motd) app

This component provides an API, serving famous quotes from movies, games, etc.

## Running this component on your workstation

Use these commands to run this component on your workstation:

```shell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Run this command to get a daily quote:

```shell
curl http://localhost:8081/api/v1/quote
```

```json
{"message":"That is why you fail.","source":"Yoda"}
```

Use this endpoint to refresh the daily quote:

```shell
curl -X POST http://localhost:8081/api/v1/quote
```

```json
{"message":"Chuck Norris can recite π. Backwards.","source":"Chuck Norris Facts"}
```

## Deploying with VMware Tanzu Application Platform

Use this command to deploy this component to your favorite Kubernetes cluster:

```shell
tanzu apps workload apply -f config/workload.yaml
```

The platform will take care of building, testing and deploying this component.

This component also loads some configuration from a
[Git repository](https://github.com/alexandreroman/motd-config).

Run this command to create a Kubernetes `Secret` out of this Git repository,
which will be used by the component at runtime:

```shell
kubectl apply -f config/app-operator
```

Run this command to get deployment status:

```shell
tanzu apps workload get motd-frontend
```

### Switching to PostgreSQL

This component relies on HSQLdb by default to store data:
this is an in-memory database.
You may want to switch to a persistent database by using PostgreSQL.

Use this command to create a PostgreSQL instance backed by Bitnami:

```shell
tanzu service class-claim create motd-db --class postgresql-unmanaged
```

Then deploy this component using the database instance:

```shell
tanzu apps workload apply -f config/workload-postgres.yaml
```

As you deploy this component with PostgreSQL enabled, the platform will take care
of injecting the service credentials for you.
