CREATE TABLE add_ons (
    id UUID PRIMARY KEY,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    price NUMERIC(10, 2) NOT NULL,
    is_active BOOLEAN NOT NULL,

    CONSTRAINT uk_add_on_name
        UNIQUE (name)
);