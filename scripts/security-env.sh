#!/usr/bin/env bash
# Source from project root; creates local credentials only if needed.
set -e
if [[ ! -f .env ]]; then
  printf 'Choose an admin password (8+ characters, at most 72 UTF-8 bytes).\n'
  read -r -s -p 'Admin password: ' password; printf '\n'
  if [[ ${#password} -lt 8 ]]; then printf 'Password too short.\n' >&2; exit 1; fi
  umask 077
  printf 'export JWT_SECRET=%q\nexport ADMIN_PASSWORD=%q\n' "$(openssl rand -base64 32)" "$password" > .env
  unset password
  printf 'Created private .env (ignored by Git).\n'
fi
set -a
# shellcheck disable=SC1091
source .env
set +a
: "${JWT_SECRET:?Set JWT_SECRET in .env}"
: "${ADMIN_PASSWORD:?Set ADMIN_PASSWORD in .env}"
