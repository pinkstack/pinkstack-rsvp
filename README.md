# Pinkstack RSVP Experiment

## Pinkstack RSVP Feeder

[![rsvp-feeder-shield][rsvp-feeder-shield]][rsvp-feeder-docker-hub]

Purpose of this service is to connect to [Meetup.com RSVPs WebSocket][meetup-com-rsvp-ws] and pipe the data to a Kafka topic. The feeding is done with a AVRO backed schema using Kafka's Schema Registry.

The service needs the following environment variables

```bash
SCHEMA_REGISTRY_URL=http://schema-registry:8081
BOOTSTRAP_SERVERS=kafka-broker-server:9092
```

The service can be build from this repository via following `sbt` invocation. This will pull all dependencies, compile everything, run tests and package the FeederApp into Docker image `pinkstack/rsvp-feeder` with tags `[0.1.0-SNAPSHOT, latest, local]`

```bash
sbt dockerize
```

To build and push the rsvp-feeder image into Docker Hub use the convinient combination of `dockerize` and `tagPushFeederApp`. This will create the image and tag it with 6 letter "tag" so that it can be furhter used in something like Kubernetes or HELM. Custom task `tagPushFeederApp` invokes [`tag-push-feeder-app.sh`](bin/tag-push-feeder-app.sh) BASH shell script.

```bash
sbt "doockerize;tagPushFeederApp"
```

### Deployment with `kubectl`

```bash
sbt dockerize && kubectl apply -f kubernetes/rsvp-deployment.yaml
```

### Deployment with `skaffold`

```bash
sbt dockerize && skaffold run
```

## KSQL

```bash
ksql http://localhost:8088
```

```sql
create stream rsvps with(kafka_topic='rsvps', value_format='AVRO');
describe hello_ksql;
```

[rsvp-feeder-docker-hub]: https://hub.docker.com/r/pinkstack/rsvp-feeder
[rsvp-feeder-shield]: https://img.shields.io/docker/pulls/pinkstack/rsvp-feeder
[meetup-com-rsvp-ws]: http://meetup.github.io/stream/rsvpTicker/
