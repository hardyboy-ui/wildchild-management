CREATE TABLE order_items (
                             id UUID PRIMARY KEY,

                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP NOT NULL,
                             created_by VARCHAR(255),
                             updated_by VARCHAR(255),

                             order_id UUID NOT NULL,
                             menu_item_id UUID NOT NULL,

                             quantity INTEGER NOT NULL,

                             unit_price NUMERIC(10, 2) NOT NULL,

                             kot_sent_quantity INTEGER NOT NULL,

                             special_instructions VARCHAR(500),

                             CONSTRAINT fk_order_item_order
                                 FOREIGN KEY (order_id)
                                     REFERENCES orders(id),

                             CONSTRAINT fk_order_item_menu_item
                                 FOREIGN KEY (menu_item_id)
                                     REFERENCES menu_items(id)
);