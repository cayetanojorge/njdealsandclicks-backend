#!/bin/bash
# use-env.sh

set -e

if [[ -z "$1" ]]; then
  echo "❌ Specifica il file da usare: local | compose | prod"
  exit 1
fi

SOURCE_FILE="envs/.env.$1"

if [[ ! -f "$SOURCE_FILE" ]]; then
  echo "❌ Il file $SOURCE_FILE non esiste"
  exit 1
fi

# Copia in .env nella directory attuale
cp "$SOURCE_FILE" .env

# Copia anche in ../.env (cartella root progetto)
cp "$SOURCE_FILE" ../.env

echo "✅ Ambiente $SOURCE_FILE attivato:"
echo "   📄 → backend/.env"
echo "   📄 → root/.env (per docker-compose)"
