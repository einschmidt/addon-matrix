#!/usr/bin/with-contenv bashio
# ==============================================================================
# Home Assistant Add-on: Matrix
# Generates the Matrix keys
# ==============================================================================
declare MKEY="/data/matrix_key.pem"

if ! bashio::fs.file_exists "${MKEY}"; then
  bashio::log.info "Master key does not exist. Creating..."
  /usr/local/bin/generate-keys --private-key /data/matrix_key.pem
  bashio::log.info "Created master key."
else
  bashio::log.info "Found master key. Continue."
fi
