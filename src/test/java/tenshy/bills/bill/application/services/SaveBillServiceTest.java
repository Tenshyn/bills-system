package tenshy.bills.bill.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tenshy.bills.bill.application.dtos.BillDTO;
import tenshy.bills.bill.application.dtos.SaveBillDTO;
import tenshy.bills.bill.application.services.impl.SaveBillService;
import tenshy.bills.bill.domain.enums.BillStatus;
import tenshy.bills.bill.domain.models.Bill;
import tenshy.bills.bill.domain.repositories.IBillRepository;
import tenshy.bills.user.domain.models.User;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SaveBillServiceTest {

    @Mock
    private IBillRepository billRepository;

    @Captor
    private ArgumentCaptor<Bill> billCaptor;

    private SaveBillService service;
    private User testUser;
    private SaveBillDTO testSaveBillDTO;

    @BeforeEach
    public void setUp() {
        service = new SaveBillService(billRepository);

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("Tester");

        testSaveBillDTO = new SaveBillDTO(LocalDate.now().plusDays(30), 150.50, "Test Bill");
    }

    @Test
    public void shouldSaveBillAndReturnDTO() {
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> {
            final Bill billToSave = invocation.getArgument(0);

            final Bill savedBill = new Bill();
            savedBill.setId(UUID.randomUUID());
            savedBill.setDescription(billToSave.getDescription());
            savedBill.setAmount(billToSave.getAmount());
            savedBill.setDueDate(billToSave.getDueDate());
            savedBill.setStatus(billToSave.getStatus());
            savedBill.setUser(billToSave.getUser());
            return savedBill;
        });

        final BillDTO result = service.execute(testSaveBillDTO, testUser);

        verify(billRepository).save(billCaptor.capture());
        final Bill capturedBill = billCaptor.getValue();

        assertEquals(testSaveBillDTO.description(), capturedBill.getDescription());
        assertEquals(testSaveBillDTO.amount(), capturedBill.getAmount());
        assertEquals(testSaveBillDTO.dueDate(), capturedBill.getDueDate());
        assertEquals(BillStatus.PENDING, capturedBill.getStatus());

        assertEquals(testUser, capturedBill.getUser());

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(testSaveBillDTO.description(), result.getDescription());
        assertEquals(testSaveBillDTO.amount(), result.getAmount());
        assertEquals(testSaveBillDTO.dueDate(), result.getDueDate());
    }

    @Test
    public void shouldPreserveAllBillPropertiesWhenSaving() {
        final SaveBillDTO detailedDTO = new SaveBillDTO(LocalDate.of(2025, 01, 31),
                                                  299.99,
                                        "Detailed Bill");

        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> {
            final Bill billToSave = invocation.getArgument(0);
            final Bill savedBill = new Bill();
            savedBill.setId(UUID.randomUUID());
            savedBill.setDescription(billToSave.getDescription());
            savedBill.setAmount(billToSave.getAmount());
            savedBill.setDueDate(billToSave.getDueDate());
            savedBill.setStatus(billToSave.getStatus());
            savedBill.setUser(billToSave.getUser());

            return savedBill;
        });

        final BillDTO result = service.execute(detailedDTO, testUser);

        verify(billRepository).save(billCaptor.capture());
        final Bill capturedBill = billCaptor.getValue();

        assertEquals(detailedDTO.description(), capturedBill.getDescription());
        assertEquals(detailedDTO.amount(), capturedBill.getAmount());
        assertEquals(detailedDTO.dueDate(), capturedBill.getDueDate());

        assertNotNull(result.getId());
        assertEquals(detailedDTO.description(), result.getDescription());
        assertEquals(detailedDTO.amount(), result.getAmount());
        assertEquals(detailedDTO.dueDate(), result.getDueDate());
    }

}
