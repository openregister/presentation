#!/bin/bash

set -ue

function ensure_db_exists {
  psql -lqt | grep -wq $1 || createdb $1
}

function ensure_user_exists {
  psql postgres -tAc "SELECT 1 FROM pg_roles WHERE rolname='$1'" | grep -q 1 || createuser $1
}

# Set up postgres
ensure_db_exists presentation
ensure_db_exists ft_presentation
ensure_user_exists postgres
ensure_user_exists presentation

# Set up IntelliJ
if [ ! -f presentation.ipr ]; then
  ./gradlew idea
fi
