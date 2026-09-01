CREATE TABLE dining_sessions (
                                 id UUID PRIMARY KEY,

                                 created_at TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP NOT NULL,
                                 created_by VARCHAR(255),
                                 updated_by VARCHAR(255),

                                 session_number VARCHAR(50) NOT NULL,
                                 table_id UUID NOT NULL,
                                 status VARCHAR(20) NOT NULL,
                                 opened_at TIMESTAMP NOT NULL,
                                 closed_at TIMESTAMP,

                                 CONSTRAINT uk_dining_session_number
                                     UNIQUE (session_number),

                                 CONSTRAINT fk_dining_session_table
                                     FOREIGN KEY (table_id)
                                         REFERENCES cafe_tables(id)
);