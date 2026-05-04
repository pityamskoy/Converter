CREATE TABLE users(
    id UUID PRIMARY KEY,
    username VARCHAR NOT NULL,
    email VARCHAR NOT NULL UNIQUE,
    password VARCHAR NOT NULL,
    is_verified BOOLEAN NOT NULL
);

CREATE TABLE verification_codes(
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(6) NOT NULL,
    expiration TIMESTAMP NOT NULL,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id)
);

CREATE TABLE patterns(
    id UUID PRIMARY KEY,
    name VARCHAR NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id)
);

CREATE TABLE modifications(
    id UUID PRIMARY KEY,
    old_name VARCHAR,
    new_name VARCHAR,
    new_value VARCHAR,
    new_type VARCHAR,
    pattern_id UUID NOT NULL REFERENCES patterns(id)
);