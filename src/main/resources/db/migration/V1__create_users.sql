CREATE TABLE users (
                       id UUID PRIMARY KEY,

                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,
                       created_by VARCHAR(255),
                       updated_by VARCHAR(255),

                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100),

                       email VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(20),

                       password_hash VARCHAR(255) NOT NULL,

                       role VARCHAR(30) NOT NULL,

                       active BOOLEAN NOT NULL DEFAULT TRUE,

                       last_login_at TIMESTAMP,

                       CONSTRAINT uk_user_email
                           UNIQUE (email),

                       CONSTRAINT uk_user_phone_number
                           UNIQUE (phone_number)
);