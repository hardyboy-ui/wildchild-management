CREATE TABLE payments (
                          id UUID PRIMARY KEY,

                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL,
                          created_by VARCHAR(255),
                          updated_by VARCHAR(255),

                          invoice_id UUID NOT NULL,

                          payment_method VARCHAR(255) NOT NULL,

                          amount NUMERIC(12, 2) NOT NULL,

                          status VARCHAR(255) NOT NULL,

                          transaction_reference VARCHAR(255) UNIQUE,

                          CONSTRAINT fk_payment_invoice
                              FOREIGN KEY (invoice_id)
                                  REFERENCES invoices(id)
);