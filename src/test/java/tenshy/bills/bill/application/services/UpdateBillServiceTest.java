package tenshy.bills.bill.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tenshy.bills.bill.application.dtos.BillDTO;
import tenshy.bills.bill.application.dtos.UpdateBillDTO;
import tenshy.bills.bill.application.exceptions.BillNotFoundException;
import tenshy.bills.bill.application.services.impl.UpdateBillService;
import tenshy.bills.bill.domain.enums.BillStatus;
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
public class UpdateBillServiceTest {

    @Mock
    private IBillRepository billRepository;

    @InjectMocks
    private UpdateBillService updateBillService;

    private User testUser;
    private Bill testBill;
    private UpdateBillDTO updateBillDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());

        testBill = new Bill();
        testBill.setId(UUID.randomUUID());
        testBill.setDescription("Original Description");
        testBill.setDueDate(LocalDate.now().plusDays(10));
        testBill.setStatus(BillStatus.PENDING);
        testBill.setPaymentDate(null);
        testBill.setUser(testUser);
    }

    @Test
    void shouldUpdateAllFieldsWhenAllFieldsAreProvided() throws BillNotFoundException {
        final LocalDate newDueDate = LocalDate.now().plusDays(15);
        final LocalDate newPaymentDate = LocalDate.now().minusDays(1);

        updateBillDTO = new UpdateBillDTO(
                UUID.randomUUID(),
                newDueDate,
                newPaymentDate,
                50.0,
                "Updated Description",
                BillStatus.PAID
        );

        when(billRepository.findByIdAndUser_Id(any(), any()))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class))).thenAnswer(i -> i.getArgument(0));

        final BillDTO result = updateBillService.execute(updateBillDTO, testUser);

        assertEquals("Updated Description", result.getDescription());
        assertEquals(newDueDate, result.getDueDate());
        assertEquals(BillStatus.PAID, result.getStatus());
        assertEquals(newPaymentDate, result.getPaymentDate());

        verify(billRepository).findByIdAndUser_Id(any(), any());
        verify(billRepository).save(testBill);
    }

    @Test
    void shouldUpdateOnlyProvidedFieldsWhenSomeFieldsAreNull() throws BillNotFoundException {
        updateBillDTO = new UpdateBillDTO(
                UUID.randomUUID(),
                null,
                LocalDate.now(),
                50,
                "Updated Description",
                null
        );

        final LocalDate originalDueDate = testBill.getDueDate();
        final BillStatus originalStatus = testBill.getStatus();

        when(billRepository.findByIdAndUser_Id(any(), any()))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class))).thenAnswer(i -> i.getArgument(0));

        final BillDTO result = updateBillService.execute(updateBillDTO, testUser);

        assertEquals("Updated Description", result.getDescription());
        assertEquals(originalDueDate, result.getDueDate());
        assertEquals(originalStatus, result.getStatus());
        assertEquals(updateBillDTO.paymentDate(), result.getPaymentDate());

        verify(billRepository, times(1)).findByIdAndUser_Id(any(), any());
        verify(billRepository).save(testBill);
    }

    @Test
    void shouldThrowExceptionWhenBillNotFound() {
        updateBillDTO = new UpdateBillDTO(
                UUID.randomUUID(),
                LocalDate.now(),
                LocalDate.now(),
                50,
                "Updated Description",
                BillStatus.PAID
        );

        when(billRepository.findByIdAndUser_Id(any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(BillNotFoundException.class, () ->
                updateBillService.execute(updateBillDTO, testUser));

        verify(billRepository).findByIdAndUser_Id(any(), any());
        verify(billRepository, never()).save(any(Bill.class));
    }

    @Test
    void shouldSetPaymentDateToNullWhenProvidedAsNull() throws BillNotFoundException {
        updateBillDTO = new UpdateBillDTO(
                UUID.randomUUID(),
                LocalDate.now(),
                null,
                50,
                "Updated Description",
                BillStatus.PENDING
        );

        testBill.setPaymentDate(LocalDate.now());

        when(billRepository.findByIdAndUser_Id(any(), any()))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class))).thenAnswer(i -> i.getArgument(0));

        final BillDTO result = updateBillService.execute(updateBillDTO, testUser);

        assertNull(result.getPaymentDate());

        verify(billRepository).findByIdAndUser_Id(any(), any());
        verify(billRepository).save(testBill);
    }

    @Test
    void shouldUpdateBillWithCorrectDescriptionEvenIfEmpty() throws BillNotFoundException {
        updateBillDTO = new UpdateBillDTO(
                UUID.randomUUID(),
                LocalDate.now(),
                LocalDate.now(),
                5,
                "",
                BillStatus.PAID
        );

        when(billRepository.findByIdAndUser_Id(any(), any()))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class))).thenAnswer(i -> i.getArgument(0));

        final BillDTO result = updateBillService.execute(updateBillDTO, testUser);

        assertEquals("", result.getDescription());

        verify(billRepository).findByIdAndUser_Id(any(), any());
        verify(billRepository).save(testBill);
    }

    @Test
    void shouldVerifyCorrectMappingOfBillToBillDTO() throws BillNotFoundException {
        updateBillDTO = new UpdateBillDTO(
                UUID.randomUUID(),
                LocalDate.now(),
                LocalDate.now(),
                20,
                "Updated Description",
                BillStatus.PAID
        );

        when(billRepository.findByIdAndUser_Id(any(), any()))
                .thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class))).thenAnswer(i -> i.getArgument(0));

        testBill.setAmount(100.0);

        final BillDTO result = updateBillService.execute(updateBillDTO, testUser);

        assertEquals(updateBillDTO.description(), result.getDescription());
        assertEquals(updateBillDTO.dueDate(), result.getDueDate());
        assertEquals(updateBillDTO.status(), result.getStatus());
        assertEquals(updateBillDTO.paymentDate(), result.getPaymentDate());
        assertEquals(testBill.getAmount(), result.getAmount());

        verify(billRepository).findByIdAndUser_Id(any(), any());
        verify(billRepository).save(testBill);
    }

}
