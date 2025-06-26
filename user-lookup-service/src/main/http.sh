#!/usr/bin/env bash

curl -X GET http://localhost:8080/user

curl -X POST http://localhost:9000/user \
  -H "Content-Type: application/json" \
  -d '{"id":2,"username":"alice"}'