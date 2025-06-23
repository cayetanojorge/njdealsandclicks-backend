#!/bin/bash

# prima Autenticati (se non l'hai gia' fatto)
# > fly auth login

# Esempio di script per impostare i secrets su Fly.io
# NON usare in produzione con valori reali, solo come riferimento

fly secrets set \
  DB_HOST="your-supabase-host" \
  DB_PORT="5432" \
  DB_NAME="your-database-name" \
  DB_USERNAME="your-db-username" \
  DB_PASSWORD="your-db-password"


# Mostra tutti i secrets configurati sull'app
# > fly secrets list