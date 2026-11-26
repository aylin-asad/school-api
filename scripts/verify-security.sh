#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
source scripts/security-env.sh
exec python3 scripts/verify-security.py "$@"
