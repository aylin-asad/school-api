#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
source scripts/security-env.sh
docker compose up -d
for i in {1..30}; do
  if docker exec school-postgres pg_isready -U postgres -d school_db >/dev/null 2>&1; then
    exec ./gradlew bootRun
  fi
  sleep 2
done
printf 'PostgreSQL did not become ready. Check docker compose logs.\n' >&2
exit 1
