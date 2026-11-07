#!/usr/bin/env bash
set -euo pipefail
master='src/main/resources/db/changelog/db.changelog-master.yaml'
change='src/main/resources/db/changelog/changes/008-isolation-demo.yaml'
if [ ! -f "$master" ]; then echo "Run from the school-api root; missing $master"; exit 1; fi
if [ ! -f "$change" ]; then echo "Missing $change - unzip the overlay first"; exit 1; fi
if grep -q '008-isolation-demo.yaml' "$master"; then
  echo 'Part D Liquibase include already present.'
else
  printf '\n- include:\n    file: changes/008-isolation-demo.yaml\n    relativeToChangelogFile: true\n' >> "$master"
  echo 'Appended Part D include to the existing master changelog.'
fi
