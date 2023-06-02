CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       first_name VARCHAR(255) NOT NULL,
                       last_name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(30) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,
                       status VARCHAR(20) NOT NULL
);

CREATE TABLE files (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(255) NOT NULL,
                       s3_secret VARCHAR(500) NOT NULL UNIQUE,
                       s3_bucket VARCHAR(255) NOT NULL,
                       location VARCHAR(500) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,
                       status VARCHAR(20) NOT NULL
);

CREATE TABLE events (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        user_id INT NOT NULL,
                        file_id INT NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL,
                        status VARCHAR(20) NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES users(id),
                        FOREIGN KEY (file_id) REFERENCES files(id)
);

CREATE TABLE tokens (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       token VARCHAR(255) NOT NULL,
                       token_type VARCHAR(20) NOT NULL,
                       expired BOOLEAN NOT NULL,
                       revoked BOOLEAN NOT NULL,
                       user_id BIGINT(255) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,
                       status VARCHAR(20) NOT NULL
);
