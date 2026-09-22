-- Prod schema (PostgreSQL). Local default profile still uses H2 + Hibernate ddl-auto.
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    phone VARCHAR(64),
    role VARCHAR(32) NOT NULL DEFAULT 'CLIENT',
    bio TEXT,
    experience_years INTEGER,
    rating DOUBLE PRECISION DEFAULT 4.8,
    consultation_fee_inr INTEGER DEFAULT 500,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    owner_user_id BIGINT,
    astrologer_id BIGINT,
    name VARCHAR(255),
    gender VARCHAR(32),
    email VARCHAR(255),
    phone VARCHAR(64),
    notes TEXT,
    birth_date_time TIMESTAMP,
    time_zone VARCHAR(64) DEFAULT 'Asia/Kolkata',
    tz_offset_hours DOUBLE PRECISION DEFAULT 5.5,
    latitude DOUBLE PRECISION DEFAULT 26.5833,
    longitude DOUBLE PRECISION DEFAULT 85.268,
    place VARCHAR(512),
    ayanamsa VARCHAR(64),
    panchang_mode VARCHAR(32),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE consultations (
    id BIGSERIAL PRIMARY KEY,
    client_user_id BIGINT,
    astrologer_id BIGINT,
    client_profile_id BIGINT,
    slot_start TIMESTAMP,
    slot_end TIMESTAMP,
    mode VARCHAR(32) DEFAULT 'VIDEO',
    status VARCHAR(32) DEFAULT 'PENDING',
    amount_inr INTEGER,
    payment_ref VARCHAR(128),
    meet_url VARCHAR(512),
    topic VARCHAR(512),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE saved_charts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    client_profile_id BIGINT,
    title VARCHAR(255),
    birth_date_time TIMESTAMP,
    place VARCHAR(512),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    ayanamsa VARCHAR(64),
    panchang_mode VARCHAR(32),
    snapshot_json TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE learning_articles (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(64),
    slug VARCHAR(128),
    title VARCHAR(255),
    body TEXT,
    published BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE app_settings (
    key_name VARCHAR(128) PRIMARY KEY,
    value_json TEXT
);
