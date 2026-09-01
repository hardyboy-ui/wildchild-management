CREATE TABLE menu_categories (
                                 id UUID PRIMARY KEY,

                                 created_at TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP NOT NULL,
                                 created_by VARCHAR(255),
                                 updated_by VARCHAR(255),

                                 name VARCHAR(100) NOT NULL,
                                 description VARCHAR(500),
                                 is_active BOOLEAN NOT NULL,
                                 display_order INTEGER NOT NULL,

                                 CONSTRAINT uk_menu_category_name
                                     UNIQUE (name)
);