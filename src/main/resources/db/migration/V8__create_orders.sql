CREATE TABLE orders (
                        id UUID PRIMARY KEY,

                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL,
                        created_by VARCHAR(255),
                        updated_by VARCHAR(255),

                        order_number VARCHAR(50) NOT NULL,
                        dining_session_id UUID NOT NULL,
                        billing_status VARCHAR(255) NOT NULL,
                        status VARCHAR(255) NOT NULL,

                        CONSTRAINT uk_order_number
                            UNIQUE (order_number),

                        CONSTRAINT fk_order_dining_session
                            FOREIGN KEY (dining_session_id)
                                REFERENCES dining_sessions(id)
);