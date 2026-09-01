CREATE TABLE menu_item_add_ons (
                                   id UUID PRIMARY KEY,

                                   created_at TIMESTAMP NOT NULL,
                                   updated_at TIMESTAMP NOT NULL,
                                   created_by VARCHAR(255),
                                   updated_by VARCHAR(255),

                                   menu_item_id UUID NOT NULL,
                                   add_on_id UUID NOT NULL,

                                   CONSTRAINT fk_menu_item_add_on_item
                                       FOREIGN KEY (menu_item_id)
                                           REFERENCES menu_items(id),

                                   CONSTRAINT fk_menu_item_add_on_add_on
                                       FOREIGN KEY (add_on_id)
                                           REFERENCES add_ons(id),

                                   CONSTRAINT uk_menu_item_add_on
                                       UNIQUE (menu_item_id, add_on_id)
);