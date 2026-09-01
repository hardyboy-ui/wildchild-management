CREATE TABLE menu_items (
    id UUID PRIMARY KEY,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    price NUMERIC(10, 2) NOT NULL,
    image_url VARCHAR(500),
    food_type VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL,

    menu_category_id UUID NOT NULL,

    CONSTRAINT fk_menu_item_category
        FOREIGN KEY (menu_category_id)
        REFERENCES menu_categories(id)
);