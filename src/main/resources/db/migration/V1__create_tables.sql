CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       first_name VARCHAR(255) NOT NULL,
                       last_name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       status VARCHAR(20) NOT NULL
);

CREATE TABLE files (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(255) NOT NULL,
                       s3_secret VARCHAR(500) NOT NULL UNIQUE,
                       s3_bucket VARCHAR(255) NOT NULL,
                       location VARCHAR(500) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,
                       status VARCHAR(20) NOT NULL
);

CREATE TABLE events (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        user_id BIGINT,
                        file_id BIGINT,
                        status VARCHAR(20) NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES users(id),
                        FOREIGN KEY (file_id) REFERENCES files(id)
);
