ARG BUILD_FROM
# hadolint ignore=DL3006
FROM $BUILD_FROM

# Set shell
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

# Setup base
# hadolint ignore=DL3003
RUN \
    set -eux \
    \
    && apk add --no-cache --virtual .build-dependencies \
        build-base=0.5-r2 \
        git=2.34.1-r0 \
    \
    && apk add --no-cache \
        go=1.17.4-r0 \
    \
    && git clone --branch "v0.7.0" --depth=1 \
        "https://github.com/matrix-org/dendrite" /tmp/dendrite \
    \
    && cd /tmp/dendrite \
    && ./build.sh \
    && mv /tmp/dendrite/bin/* /usr/local/bin \
    \
    && rm -f -r /tmp/* \
    && apk del --no-cache --purge \
        .build-dependencies \
        go

# Copy root filesystem
COPY rootfs /

# Build arguments
ARG BUILD_ARCH
ARG BUILD_DATE
ARG BUILD_DESCRIPTION
ARG BUILD_NAME
ARG BUILD_REF
ARG BUILD_REPOSITORY
ARG BUILD_VERSION

# Labels
LABEL \
    io.hass.name="${BUILD_NAME}" \
    io.hass.description="${BUILD_DESCRIPTION}" \
    io.hass.arch="${BUILD_ARCH}" \
    io.hass.type="addon" \
    io.hass.version=${BUILD_VERSION} \
    maintainer="Einschmidt" \
    org.opencontainers.image.title="${BUILD_NAME}" \
    org.opencontainers.image.description="${BUILD_DESCRIPTION}" \
    org.opencontainers.image.vendor="Einschmidt" \
    org.opencontainers.image.authors="einschmidt" \
    org.opencontainers.image.licenses="MIT" \
    org.opencontainers.image.url="https://google.com" \
    org.opencontainers.image.source="https://github.com/${BUILD_REPOSITORY}" \
    org.opencontainers.image.documentation="https://github.com/${BUILD_REPOSITORY}/blob/main/README.md" \
    org.opencontainers.image.created=${BUILD_DATE} \
    org.opencontainers.image.revision=${BUILD_REF} \
    org.opencontainers.image.version=${BUILD_VERSION}
