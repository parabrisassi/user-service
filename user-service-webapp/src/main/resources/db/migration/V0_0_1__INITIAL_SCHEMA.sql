CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY    NOT NULL,
    username        VARCHAR(64)              NOT NULL
);

CREATE UNIQUE INDEX users_username_unique_index
    ON users (username);

CREATE TABLE user_credentials (
    id              BIGSERIAL PRIMARY KEY    NOT NULL,
    user_id         INTEGER                  NOT NULL,
    hashed_password VARCHAR                  NOT NULL,
    created_at      TIMESTAMP                NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE user_roles (
    user_id INTEGER     NOT NULL,
    role    VARCHAR(64) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
