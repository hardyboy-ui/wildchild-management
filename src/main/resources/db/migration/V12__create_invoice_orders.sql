CREATE TABLE invoice_orders (
                                id UUID PRIMARY KEY,

                                created_at TIMESTAMP NOT NULL,
                                updated_at TIMESTAMP NOT NULL,
                                created_by VARCHAR(255),
                                updated_by VARCHAR(255),

                                invoice_id UUID NOT NULL,
                                order_id UUID NOT NULL,

                                CONSTRAINT uk_invoice_order
                                    UNIQUE (invoice_id, order_id),

                                CONSTRAINT fk_invoice_order_invoice
                                    FOREIGN KEY (invoice_id)
                                        REFERENCES invoices(id),

                                CONSTRAINT fk_invoice_order_order
                                    FOREIGN KEY (order_id)
                                        REFERENCES orders(id)
);