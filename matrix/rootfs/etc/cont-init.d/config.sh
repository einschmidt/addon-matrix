#!/usr/bin/with-contenv bashio
# ==============================================================================
# Home Assistant Add-on: Matrix
# Create config
# ==============================================================================
# Generate configuration.
bashio::log.info "Creating configuration..."
bashio::var.json \
    server_name "$(bashio::config 'server_name')" \
    | tempio \
        -template /etc/dendrite/templates/config.gtpl \
        -out /etc/dendrite/config.yaml
bashio::log.info "Done creating configuration."