# Pinkstack RSVP Experiment

## Pinkstack RSVP Feeder

[![rsvp-feeder-shield][rsvp-feeder-shield]][rsvp-feeder-docker-hub]

Purpose of this micro-service is to connect to Meetup.com RSVPs WebSocket and pipe the data to Kafka topic. The feeding is done with a AVRO backed schema using Kafka's Schema Registry.

The service needs the following environment variables

```bash
SCHEMA_REGISTRY_URL=http://schema-registry:8081
BOOTSTRAP_SERVERS=kafka-broker-server:9092
```

The service can be build from this repository via following `sbt` invocation. This will pull all dependencies, compile everything, run tests and package the FeederApp into Docker image `pinkstack/rsvp-feeder` with tags `[0.1.0-SNAPSHOT, latest, local]`

```bash
sbt dockerize
```

To build and push the rsvp-feeder into Docker Hub use the convinient combination of `dockerize` and `tagPushFeederApp`. This will create the image and tag it with 6 letter "tag" so that it can be furhter used in something like Kubernetes or HELM.

```bash
sbt "doockerize;tagPushFeederApp"
```

[rsvp-feeder-docker-hub]: https://hub.docker.com/r/pinkstack/rsvp-feeder
[rsvp-feeder-shield]: https://img.shields.io/docker/pulls/pinkstack/rsvp-feeder
