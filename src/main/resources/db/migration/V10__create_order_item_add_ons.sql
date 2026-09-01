CREATE TABLE order_item_add_ons (
                                    id UUID PRIMARY KEY,

                                    created_at TIMESTAMP NOT NULL,
                                    updated_at TIMESTAMP NOT NULL,
                                    created_by VARCHAR(255),
                                    updated_by VARCHAR(255),

                                    order_item_id UUID NOT NULL,
                                    add_on_id UUID NOT NULL,

                                    unit_price NUMERIC(10, 2) NOT NULL,

                                    CONSTRAINT fk_order_item_add_on_order_item
                                        FOREIGN KEY (order_item_id)
                                            REFERENCES order_items(id),

                                    CONSTRAINT fk_order_item_add_on_add_on
                                        FOREIGN KEY (add_on_id)
                                            REFERENCES add_ons(id),

                                    CONSTRAINT uk_order_item_add_on
                                        UNIQUE (order_item_id, add_on_id)
);