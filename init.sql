CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS files (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    hash VARCHAR(255) NOT NULL,
    upload_date TIMESTAMP NOT NULL,
    file_content BYTEA NOT NULL,
    userID BIGINT NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (userID) REFERENCES users(id) ON DELETE CASCADE
    );

INSERT INTO users (login, password)
VALUES ('string', '$2a$10$PeNz5ZqtTjWAOe4vZF8VyuPtuZ9MEJhE7.EG62unJ5GEBJJ6fA5vq')
    ON CONFLICT (login) DO NOTHING;
INSERT INTO users (login, password)
VALUES ('user', '$2a$10$9sMioSuSBOhBqu1ktW.93eSq4r0BiE7aaOG/C2qJTbfFHWa.QTk7e')
    ON CONFLICT (login) DO NOTHING;