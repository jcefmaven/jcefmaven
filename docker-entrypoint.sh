#!/usr/bin/env sh

sed "s|{{WORKSPACE_DIR}}|${WORKSPACE_DIR}|;s|{{CRAWL_INTERVAL_MS}}|${CRAWL_INTERVAL_MS}|" /app/config.template.json > /app/config.json

exec "$@"
