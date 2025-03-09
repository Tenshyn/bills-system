package tenshy.bills.bill.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import tenshy.bills.bill.domain.enums.BillStatus;
import tenshy.bills.bill.domain.exceptions.AlreadyPaidBillException;
import tenshy.bills.bill.domain.models.Bill;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BillTest {

    private Bill bill;

    @BeforeEach
    void setUp() {
        bill = new Bill();
        bill.setId(UUID.randomUUID());
        bill.setAmount(100.0);
        bill.setDescription("Test bill");
        bill.setStatus(BillStatus.PENDING);
    }

    @Test
    void testPayWhenBillIsNotOverdue() throws AlreadyPaidBillException {
        final LocalDate today = LocalDate.now();
        final LocalDate tomorrow = today.plusDays(1);
        bill.setDueDate(tomorrow);

        bill.pay();

        assertEquals(BillStatus.PAID, bill.getStatus());
        assertEquals(today, bill.getPaymentDate());
    }

    @Test
    void testPayWhenBillIsOverdue() throws AlreadyPaidBillException {
        final LocalDate today = LocalDate.now();
        final LocalDate yesterday = today.minusDays(1);
        bill.setDueDate(yesterday);

        bill.pay();

        assertEquals(BillStatus.LATE_PAYMENT, bill.getStatus());
        assertEquals(today, bill.getPaymentDate());
    }

    @Test
    void testPayWhenBillIsDueToday() throws AlreadyPaidBillException {
        final LocalDate today = LocalDate.now();
        bill.setDueDate(today);

        bill.pay();

        assertEquals(BillStatus.PAID, bill.getStatus());
        assertEquals(today, bill.getPaymentDate());
    }

}
