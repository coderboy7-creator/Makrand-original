#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
echo "Start API:  mvn spring-boot:run"
echo "Start Web:  cd frontend && npm run dev"
echo "Demo users: user@ / astro@ / admin@ makaranda.app  passwords user123 astro123 admin123"
