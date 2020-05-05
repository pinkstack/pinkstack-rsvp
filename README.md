# Pinkstack RSVP Experiment

## Pinkstack RSVP Feeder

![Docker Image](https://img.shields.io/docker/pulls/pinkstack/rsvp-feeder)

Purpose of this micro-service is to connect to Meetup.com RSVPs WebSocket feed and pipe the data to Kafka topic. The feeding is done with a AVRO backed schema using Kafka's Schema Registry.

