package tenshy.bills.bill.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tenshy.bills.bill.application.exceptions.BillNotFoundException;
import tenshy.bills.bill.application.services.impl.PayBillService;
import tenshy.bills.bill.domain.enums.BillStatus;
import tenshy.bills.bill.domain.exceptions.AlreadyPaidBillException;
import tenshy.bills.bill.domain.models.Bill;
import tenshy.bills.bill.domain.repositories.IBillRepository;
import tenshy.bills.user.domain.models.User;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PayBillServiceTest {

    @Mock
    private IBillRepository billRepository;

    @Captor
    private ArgumentCaptor<Bill> billCaptor;

    private PayBillService service;
    private User testUser;
    private UUID testBillId;
    private Bill testBill;

    @BeforeEach
    public void setUp() {
        service = new PayBillService(billRepository);

        testUser = new User();
        testUser.setId(UUID.randomUUID());

        testBillId = UUID.randomUUID();

        testBill = new Bill(testBillId, LocalDate.now().plusDays(5), 100.0, "Test Bill", testUser);
    }

    @Test
    public void shouldMarkBillAsPaidWhenBillExists() throws BillNotFoundException, AlreadyPaidBillException {
        when(billRepository.findByIdAndUser_Id(testBillId, testUser.getId())).thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);

        service.execute(testBillId, testUser);

        verify(billRepository).findByIdAndUser_Id(testBillId, testUser.getId());
        verify(billRepository).save(billCaptor.capture());

        final Bill savedBill = billCaptor.getValue();

        assertEquals(BillStatus.PAID, savedBill.getStatus());
        assertNotNull(savedBill.getPaymentDate());
    }

    @Test
    public void shouldThrowExceptionWhenBillNotFound() {
        when(billRepository.findByIdAndUser_Id(testBillId, testUser.getId())).thenReturn(Optional.empty());

        assertThrows(BillNotFoundException.class, () -> {
            service.execute(testBillId, testUser);
        });

        verify(billRepository).findByIdAndUser_Id(testBillId, testUser.getId());
        verify(billRepository, never()).save(any(Bill.class));
    }

    @Test
    public void shouldUseCorrectPaymentDateWhenPaying() throws BillNotFoundException, AlreadyPaidBillException {
        final LocalDate today = LocalDate.now();
        when(billRepository.findByIdAndUser_Id(testBillId, testUser.getId())).thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);

        service.execute(testBillId, testUser);

        verify(billRepository).save(billCaptor.capture());

        final Bill savedBill = billCaptor.getValue();

        assertEquals(today, savedBill.getPaymentDate());
    }

    @Test
    public void shouldVerifyBillPayMethodIsCalled() throws BillNotFoundException, AlreadyPaidBillException {
        final Bill spyBill = spy(testBill);
        when(billRepository.findByIdAndUser_Id(testBillId, testUser.getId())).thenReturn(Optional.of(spyBill));

        service.execute(testBillId, testUser);

        verify(spyBill).pay();
        verify(billRepository).save(spyBill);
    }

    @Test
    public void shouldHandleAlreadyPaidBill() throws BillNotFoundException, AlreadyPaidBillException {
        testBill.setStatus(BillStatus.PAID);
        testBill.setPaymentDate(LocalDate.now().minusDays(1));

        final Bill spyBill = spy(testBill);
        when(billRepository.findByIdAndUser_Id(testBillId, testUser.getId())).thenReturn(Optional.of(spyBill));

        assertThrows(AlreadyPaidBillException.class, () -> service.execute(testBillId, testUser));

        verify(spyBill).pay();
        verify(billRepository, never()).save(spyBill);
    }


}
