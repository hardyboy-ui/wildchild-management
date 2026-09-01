CREATE TABLE cafe_tables (
                             id UUID PRIMARY KEY,

                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP NOT NULL,
                             created_by VARCHAR(255),
                             updated_by VARCHAR(255),

                             table_number VARCHAR(50) NOT NULL,
                             capacity INTEGER NOT NULL,
                             display_order INTEGER NOT NULL,
                             is_active BOOLEAN NOT NULL,

                             CONSTRAINT uk_cafe_table_number
                                 UNIQUE (table_number)
);