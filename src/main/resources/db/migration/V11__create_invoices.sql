CREATE TABLE invoices (
                          id UUID PRIMARY KEY,

                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL,
                          created_by VARCHAR(255),
                          updated_by VARCHAR(255),

                          invoice_number VARCHAR(255) NOT NULL UNIQUE,

                          billing_type VARCHAR(255) NOT NULL,

                          subtotal NUMERIC(12, 2) NOT NULL,

                          grand_total NUMERIC(12, 2) NOT NULL,

                          status VARCHAR(255) NOT NULL,

                          discount_type VARCHAR(255),

                          discount_value NUMERIC(12, 2),

                          discount_amount NUMERIC(12, 2) NOT NULL,

                          tax_rate NUMERIC(5, 2) NOT NULL,

                          tax_amount NUMERIC(12, 2) NOT NULL
);