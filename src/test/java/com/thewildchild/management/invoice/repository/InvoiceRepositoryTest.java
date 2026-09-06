package com.thewildchild.management.invoice.repository;

import com.thewildchild.management.invoice.entity.Invoice;
import com.thewildchild.management.invoice.entity.InvoiceBillingType;
import com.thewildchild.management.invoice.entity.InvoiceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class InvoiceRepositoryIntegrationTest {

    @Autowired
    private InvoiceRepository invoiceRepository;


    // ============================================================
    // 1. Count invoices created between dates
    // ============================================================

    @Test
    void shouldCountInvoicesCreatedBetweenDates() {

        // Arrange

        LocalDateTime start =
                LocalDateTime.now().minusHours(2);

        LocalDateTime end =
                LocalDateTime.now().plusHours(1);


        Invoice invoice1 = createInvoice(
                "INV-TEST-COUNT-01",
                InvoiceStatus.GENERATED
        );

        Invoice invoice2 = createInvoice(
                "INV-TEST-COUNT-02",
                InvoiceStatus.PAID
        );


        invoiceRepository.save(invoice1);
        invoiceRepository.save(invoice2);

        invoiceRepository.flush();


        // Act

        long count =
                invoiceRepository
                        .countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                start,
                                end
                        );


        // Assert

        assertThat(count)
                .isEqualTo(2);
    }


    // ============================================================
    // 2. Count invoices by status and date range
    // ============================================================

    @Test
    void shouldCountInvoicesByStatusAndDateRange() {

        // Arrange

        LocalDateTime start =
                LocalDateTime.now().minusHours(2);

        LocalDateTime end =
                LocalDateTime.now().plusHours(1);


        Invoice generatedInvoice = createInvoice(
                "INV-TEST-STATUS-01",
                InvoiceStatus.GENERATED
        );

        Invoice paidInvoice = createInvoice(
                "INV-TEST-STATUS-02",
                InvoiceStatus.PAID
        );

        Invoice cancelledInvoice = createInvoice(
                "INV-TEST-STATUS-03",
                InvoiceStatus.CANCELLED
        );


        invoiceRepository.save(generatedInvoice);
        invoiceRepository.save(paidInvoice);
        invoiceRepository.save(cancelledInvoice);

        invoiceRepository.flush();


        // Act

        long paidCount =
                invoiceRepository
                        .countByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                InvoiceStatus.PAID,
                                start,
                                end
                        );


        // Assert

        assertThat(paidCount)
                .isEqualTo(1);
    }


    // ============================================================
    // 3. Get sales between dates
    // ============================================================

    @Test
    void shouldGetSalesBetweenDates() {

        // Arrange

        LocalDateTime start =
                LocalDateTime.now().minusHours(2);

        LocalDateTime end =
                LocalDateTime.now().plusHours(1);


        Invoice invoice1 = createInvoice(
                "INV-TEST-SALES-01",
                InvoiceStatus.PAID
        );

        invoice1.setSubtotal(
                new BigDecimal("1000.00")
        );

        invoice1.setDiscountAmount(
                new BigDecimal("100.00")
        );

        invoice1.setTaxRate(
                new BigDecimal("18.00")
        );

        invoice1.setTaxAmount(
                new BigDecimal("162.00")
        );

        invoice1.setGrandTotal(
                new BigDecimal("1062.00")
        );


        Invoice invoice2 = createInvoice(
                "INV-TEST-SALES-02",
                InvoiceStatus.PAID
        );

        invoice2.setSubtotal(
                new BigDecimal("500.00")
        );

        invoice2.setDiscountAmount(
                new BigDecimal("50.00")
        );

        invoice2.setTaxRate(
                new BigDecimal("18.00")
        );

        invoice2.setTaxAmount(
                new BigDecimal("81.00")
        );

        invoice2.setGrandTotal(
                new BigDecimal("531.00")
        );


        Invoice generatedInvoice = createInvoice(
                "INV-TEST-SALES-03",
                InvoiceStatus.GENERATED
        );

        generatedInvoice.setSubtotal(
                new BigDecimal("9999.00")
        );

        generatedInvoice.setDiscountAmount(
                BigDecimal.ZERO
        );

        generatedInvoice.setTaxRate(
                BigDecimal.ZERO
        );

        generatedInvoice.setTaxAmount(
                BigDecimal.ZERO
        );

        generatedInvoice.setGrandTotal(
                new BigDecimal("9999.00")
        );


        invoiceRepository.save(invoice1);
        invoiceRepository.save(invoice2);
        invoiceRepository.save(generatedInvoice);

        invoiceRepository.flush();


        // Act

        var result =
                invoiceRepository.getSalesBetween(
                        InvoiceStatus.PAID,
                        start,
                        end
                );


        // Assert

        assertThat(result)
                .isNotNull();

        assertThat(result.getGrossSales())
                .isEqualByComparingTo("1500.00");

        assertThat(result.getTotalDiscount())
                .isEqualByComparingTo("150.00");

        assertThat(result.getTotalTax())
                .isEqualByComparingTo("243.00");

        assertThat(result.getNetSales())
                .isEqualByComparingTo("1593.00");
    }


    // ============================================================
    // 4. Find outstanding invoices
    // ============================================================

    @Test
    void shouldFindOutstandingInvoices() {

        // Arrange

        Invoice generatedInvoice = createInvoice(
                "INV-TEST-OUTSTANDING-01",
                InvoiceStatus.GENERATED
        );

        Invoice partiallyPaidInvoice = createInvoice(
                "INV-TEST-OUTSTANDING-02",
                InvoiceStatus.PARTIALLY_PAID
        );

        Invoice paidInvoice = createInvoice(
                "INV-TEST-OUTSTANDING-03",
                InvoiceStatus.PAID
        );

        Invoice cancelledInvoice = createInvoice(
                "INV-TEST-OUTSTANDING-04",
                InvoiceStatus.CANCELLED
        );


        invoiceRepository.save(generatedInvoice);
        invoiceRepository.save(partiallyPaidInvoice);
        invoiceRepository.save(paidInvoice);
        invoiceRepository.save(cancelledInvoice);

        invoiceRepository.flush();


        // Act

        List<Invoice> results =
                invoiceRepository.findOutstandingInvoices();


        // Assert

        assertThat(results)
                .hasSize(2);

        assertThat(results)
                .extracting(Invoice::getStatus)
                .containsExactlyInAnyOrder(
                        InvoiceStatus.GENERATED,
                        InvoiceStatus.PARTIALLY_PAID
                );
    }


    // ============================================================
    // Helper method
    // ============================================================

    private Invoice createInvoice(
            String invoiceNumber,
            InvoiceStatus status
    ) {

        Invoice invoice = new Invoice();

        invoice.setInvoiceNumber(invoiceNumber);

        invoice.setBillingType(
                InvoiceBillingType.ORDER
        );

        invoice.setSubtotal(
                new BigDecimal("1000.00")
        );

        invoice.setDiscountAmount(
                BigDecimal.ZERO
        );

        invoice.setTaxRate(
                BigDecimal.ZERO
        );

        invoice.setTaxAmount(
                BigDecimal.ZERO
        );

        invoice.setGrandTotal(
                new BigDecimal("1000.00")
        );

        invoice.setStatus(status);

        return invoice;
    }
}