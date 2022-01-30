# This is the Dendrite configuration file.
#
# The configuration is split up into sections - each Dendrite component has a
# configuration section, in addition to the "global" section which applies to
# all components.
#
# At a minimum, to get started, you will need to update the settings in the
# "global" section for your deployment, and you will need to check that the
# database "connection_string" line in each component section is correct. 
#
# Each component with a "database" section can accept the following formats
# for "connection_string":
#   SQLite:     file:filename.db
#               file:///path/to/filename.db
#   PostgreSQL: postgresql://user:pass@hostname/database?params=...
#
# SQLite is embedded into Dendrite and therefore no further prerequisites are
# needed for the database when using SQLite mode. However, performance with
# PostgreSQL is significantly better and recommended for multi-user deployments.
# SQLite is typically around 20-30% slower than PostgreSQL when tested with a
# small number of users and likely will perform worse still with a higher volume
# of users.
#
# The "max_open_conns" and "max_idle_conns" settings configure the maximum 
# number of open/idle database connections. The value 0 will use the database
# engine default, and a negative value will use unlimited connections. The
# "conn_max_lifetime" option controls the maximum length of time a database
# connection can be idle in seconds - a negative value is unlimited.

# The version of the configuration file. 
version: 2

# Global Matrix configuration. This configuration applies to all components.
global:
  # The domain name of this homeserver.
  server_name: {{ .server_name }}

  # The path to the signing private key file, used to sign requests and events.
  # Note that this is NOT the same private key as used for TLS! To generate a
  # signing key, use "./bin/generate-keys --private-key matrix_key.pem".
  private_key: /data/matrix_key.pem

  # The paths and expiry timestamps (as a UNIX timestamp in millisecond precision)
  # to old signing private keys that were formerly in use on this domain. These
  # keys will not be used for federation request or event signing, but will be
  # provided to any other homeserver that asks when trying to verify old events.
  # old_private_keys:
  # - private_key: old_matrix_key.pem
  #   expired_at: 1601024554498

  # How long a remote server can cache our server signing key before requesting it
  # again. Increasing this number will reduce the number of requests made by other
  # servers for our key but increases the period that a compromised key will be
  # considered valid by other homeservers.
  key_validity_period: 168h0m0s

  # The server name to delegate server-server communications to, with optional port
  # e.g. localhost:443
  well_known_server_name: ""

  # Lists of domains that the server will trust as identity servers to verify third
  # party identifiers such as phone numbers and email addresses.
  trusted_third_party_id_servers:
  - matrix.org
  - vector.im

  # Disables federation. Dendrite will not be able to make any outbound HTTP requests
  # to other servers and the federation API will not be exposed.
  disable_federation: true

  # Configuration for NATS JetStream
  jetstream:
    # A list of NATS Server addresses to connect to. If none are specified, an
    # internal NATS server will be started automatically when running Dendrite
    # in monolith mode. It is required to specify the address of at least one
    # NATS Server node if running in polylith mode.
    addresses:
      # - localhost:4222
    # Keep all NATS streams in memory, rather than persisting it to the storage
    # path below. This option is present primarily for integration testing and
    # should not be used on a real world Dendrite deployment.
    in_memory: false
    # Persistent directory to store JetStream streams in. This directory 
    # should be preserved across Dendrite restarts.
    storage_path: /data/
    # The prefix to use for stream names for this homeserver - really only
    # useful if running more than one Dendrite on the same NATS deployment.
    topic_prefix: Dendrite

  # Configuration for Prometheus metric collection.
  metrics:
    # Whether or not Prometheus metrics are enabled.
    enabled: false

    # HTTP basic authentication to protect access to monitoring.
    basic_auth:
      username: metrics
      password: metrics

  # DNS cache options. The DNS cache may reduce the load on DNS servers
  # if there is no local caching resolver available for use.
  dns_cache:
    # Whether or not the DNS cache is enabled.
    enabled: false

    # Maximum number of entries to hold in the DNS cache, and
    # for how long those items should be considered valid in seconds.
    cache_size: 256
    cache_lifetime: "5m" # 5minutes; see https://pkg.go.dev/time@master#ParseDuration for more

# Configuration for the Appservice API.
app_service_api:
  internal_api:
    listen: http://localhost:7777
    connect: http://localhost:7777
  database:
    connection_string: file:///data/appservice.db
    max_open_conns: 10
    max_idle_conns: 2
    conn_max_lifetime: -1

  # Disable the validation of TLS certificates of appservices. This is
  # not recommended in production since it may allow appservice traffic
  # to be sent to an unverified endpoint.
  disable_tls_validation: false

  # Appservice configuration files to load into this homeserver.
  config_files: []

# Configuration for the Client API.
client_api:
  internal_api:
    listen: http://localhost:7771
    connect: http://localhost:7771
  external_api:
    listen: http://[::]:8071

  # Prevents new users from being able to register on this homeserver, except when
  # using the registration shared secret below.
  registration_disabled: false

  # If set, allows registration by anyone who knows the shared secret, regardless of
  # whether registration is otherwise disabled.
  registration_shared_secret: ""

  # Whether to require reCAPTCHA for registration.
  enable_registration_captcha: false

  # Settings for ReCAPTCHA. 
  recaptcha_public_key: ""
  recaptcha_private_key: ""
  recaptcha_bypass_secret: ""
  recaptcha_siteverify_api: ""

  # TURN server information that this homeserver should send to clients. 
  turn:
    turn_user_lifetime: ""
    turn_uris: []
    turn_shared_secret: ""
    turn_username: ""
    turn_password: ""

  # Settings for rate-limited endpoints. Rate limiting will kick in after the
  # threshold number of "slots" have been taken by requests from a specific 
  # host. Each "slot" will be released after the cooloff time in milliseconds.
  rate_limiting:
    enabled: true
    threshold: 5
    cooloff_ms: 500

# Configuration for the EDU server.
edu_server:
  internal_api:
    listen: http://localhost:7778
    connect: http://localhost:7778

# Configuration for the Federation API.
federation_api:
  internal_api:
    listen: http://localhost:7772
    connect: http://localhost:7772
  external_api:
    listen: http://[::]:8072
  database:
    connection_string: file:///data/federationapi.db
    max_open_conns: 10
    max_idle_conns: 2
    conn_max_lifetime: -1

  # List of paths to X.509 certificates to be used by the external federation listeners.
  # These certificates will be used to calculate the TLS fingerprints and other servers
  # will expect the certificate to match these fingerprints. Certificates must be in PEM
  # format.
  federation_certificates: []

  # How many times we will try to resend a failed transaction to a specific server. The
  # backoff is 2**x seconds, so 1 = 2 seconds, 2 = 4 seconds, 3 = 8 seconds etc.
  send_max_retries: 16

  # Disable the validation of TLS certificates of remote federated homeservers. Do not
  # enable this option in production as it presents a security risk!
  disable_tls_validation: false

  # Use the following proxy server for outbound federation traffic.
  proxy_outbound:
    enabled: false
    protocol: http
    host: localhost
    port: 8080

  # Perspective keyservers to use as a backup when direct key fetches fail. This may
  # be required to satisfy key requests for servers that are no longer online when
  # joining some rooms.
  key_perspectives:
  - server_name: matrix.org
    keys:
    - key_id: ed25519:auto
      public_key: Noi6WqcDj0QmPxCNQqgezwTlBKrfqehY1u2FyWP9uYw
    - key_id: ed25519:a_RXGa
      public_key: l8Hft5qXKn1vfHrg3p4+W8gELQVo8N13JkluMfmn2sQ
  # This option will control whether Dendrite will prefer to look up keys directly
  # or whether it should try perspective servers first, using direct fetches as a
  # last resort.
  prefer_direct_fetch: false

# Configuration for the Key Server (for end-to-end encryption).
key_server:
  internal_api:
    listen: http://localhost:7779
    connect: http://localhost:7779
  database:
    connection_string: file:///data/keyserver.db
    max_open_conns: 10
    max_idle_conns: 2
    conn_max_lifetime: -1

# Configuration for the Media API.
media_api:
  internal_api:
    listen: http://localhost:7774
    connect: http://localhost:7774
  external_api:
    listen: http://[::]:8074
  database:
    connection_string: file:///data/mediaapi.db
    max_open_conns: 5
    max_idle_conns: 2
    conn_max_lifetime: -1

  # Storage path for uploaded media. May be relative or absolute.
  base_path: ./media_store

  # The maximum allowed file size (in bytes) for media uploads to this homeserver
  # (0 = unlimited). If using a reverse proxy, ensure it allows requests at
  # least this large (e.g. client_max_body_size in nginx.)
  max_file_size_bytes: 10485760

  # Whether to dynamically generate thumbnails if needed.
  dynamic_thumbnails: false

  # The maximum number of simultaneous thumbnail generators to run.
  max_thumbnail_generators: 10

  # A list of thumbnail sizes to be generated for media content.
  thumbnail_sizes:
  - width: 32
    height: 32
    method: crop
  - width: 96
    height: 96
    method: crop
  - width: 640
    height: 480
    method: scale

# Configuration for experimental MSC's
mscs:
  # A list of enabled MSC's
  # Currently valid values are:
  # - msc2836    (Threading, see https://github.com/matrix-org/matrix-doc/pull/2836)
  # - msc2946    (Spaces Summary, see https://github.com/matrix-org/matrix-doc/pull/2946)
  mscs: []
  database:
    connection_string: file:///data/mscs.db
    max_open_conns: 5
    max_idle_conns: 2
    conn_max_lifetime: -1

# Configuration for the Room Server.
room_server:
  internal_api:
    listen: http://localhost:7770
    connect: http://localhost:7770
  database:
    connection_string: file:///data/roomserver.db
    max_open_conns: 10
    max_idle_conns: 2
    conn_max_lifetime: -1

# Configuration for the Sync API.
sync_api:
  internal_api:
    listen: http://localhost:7773
    connect: http://localhost:7773
  external_api:
    listen: http://[::]:8073
  database:
    connection_string: file:///data/syncapi.db
    max_open_conns: 10
    max_idle_conns: 2
    conn_max_lifetime: -1

  # This option controls which HTTP header to inspect to find the real remote IP
  # address of the client. This is likely required if Dendrite is running behind
  # a reverse proxy server.
  # real_ip_header: X-Real-IP

# Configuration for the User API.
user_api:
  # The cost when hashing passwords on registration/login. Default: 10. Min: 4, Max: 31
  # See https://pkg.go.dev/golang.org/x/crypto/bcrypt for more information.
  # Setting this lower makes registration/login consume less CPU resources at the cost of security
  # should the database be compromised. Setting this higher makes registration/login consume more
  # CPU resources but makes it harder to brute force password hashes.
  # This value can be low if performing tests or on embedded Dendrite instances (e.g WASM builds)
  # bcrypt_cost: 10
  internal_api:
    listen: http://localhost:7781
    connect: http://localhost:7781
  account_database:
    connection_string: file:///data/userapi_accounts.db
    max_open_conns: 10
    max_idle_conns: 2
    conn_max_lifetime: -1
  device_database:
    connection_string: file:///data/userapi_devices.db
    max_open_conns: 10
    max_idle_conns: 2
    conn_max_lifetime: -1
  # The length of time that a token issued for a relying party from 
  # /_matrix/client/r0/user/{userId}/openid/request_token endpoint
  # is considered to be valid in milliseconds. 
  # The default lifetime is 3600000ms (60 minutes).
  # openid_token_lifetime_ms: 3600000

# Configuration for Opentracing.
# See https://github.com/matrix-org/dendrite/tree/master/docs/tracing for information on
# how this works and how to set it up.
tracing:
  enabled: false
  jaeger:
    serviceName: ""
    disabled: false
    rpc_metrics: false
    tags: []
    sampler: null
    reporter: null
    headers: null
    baggage_restrictions: null
    throttler: null

# Logging configuration
logging:
- type: std
  level: info
- type: file
  # The logging level, must be one of debug, info, warn, error, fatal, panic.
  level: info
  params:
    path: ./logs