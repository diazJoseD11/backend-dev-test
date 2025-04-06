#!/bin/bash

host="$1"
port="$2"
shift 2

echo "Waiting for PostgresSQL on $host:$port..."
while ! nc -z "$host" "$port"; do
  sleep 1
done

echo "PostgresSQL is ready. Starting application..."
exec "$@"
