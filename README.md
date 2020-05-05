# Pinkstack RSVP Experiment

## Pinkstack RSVP Feeder

![rsvp-feeder-docker-hub][rsvp-feeder-shield]

Purpose of this micro-service is to connect to Meetup.com RSVPs WebSocket feed and pipe the data to Kafka topic. The feeding is done with a AVRO backed schema using Kafka's Schema Registry.

[rsvp-feeder-docker-hub]: https://hub.docker.com/r/pinkstack/rsvp-feeder
[rsvp-feeder-shield]: https://img.shields.io/docker/pulls/pinkstack/rsvp-feeder
