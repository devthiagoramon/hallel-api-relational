#!/usr/bin/env bash

if [ -z "$1" ]; then
  echo "Uso: $0 <hostname>"
  exit 1
fi

if [ -z "$2" ]; then
  echo "Passe o nome do arquivo"
  exit 1
fi

ssh-keygen -R "$1"

echo "Chave com os hosts resetados"

clip < ~/.ssh/"$2"

echo "Chave copiada para a area de transferência"