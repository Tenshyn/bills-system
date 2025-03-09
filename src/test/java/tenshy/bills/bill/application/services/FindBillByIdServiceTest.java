package tenshy.bills.bill.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tenshy.bills.bill.application.dtos.BillDTO;
import tenshy.bills.bill.application.exceptions.BillNotFoundException;
import tenshy.bills.bill.application.mappers.BillMapper;
import tenshy.bills.bill.application.services.impl.FindBillByIdService;
import tenshy.bills.bill.domain.models.Bill;
import tenshy.bills.bill.domain.repositories.IBillRepository;
import tenshy.bills.user.domain.models.User;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FindBillByIdServiceTest {

    @Mock
    private IBillRepository billRepository;

    private FindBillByIdService service;

    private final UUID billId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();


    @BeforeEach
    public void setUp() {
        service = new FindBillByIdService(billRepository);
    }

    @Test
    public void shouldReturnBillDTOWhenBillExists() throws BillNotFoundException {
        final LocalDate dueDate = LocalDate.of(2025, 1, 31);

        final User user = new User();
        user.setId(userId);

        final Bill bill = new Bill(billId, dueDate, 100.0, "Test Bill", user);

        when(billRepository.findByIdAndUser_Id(billId, userId))
                .thenReturn(Optional.of(bill));

        final BillDTO expectedDTO = BillMapper.dtoFromEntity(bill);

        final BillDTO result = service.execute(billId, user);

        assertNotNull(result);
        assertEquals(expectedDTO.getId(), result.getId());
        assertEquals(expectedDTO.getDescription(), result.getDescription());
        assertEquals(expectedDTO.getAmount(), result.getAmount());

        verify(billRepository, times(1)).findByIdAndUser_Id(billId, userId);
    }

    @Test
    public void shouldThrowExceptionWhenBillNotFound() {
        final User user = new User();
        user.setId(userId);

        when(billRepository.findByIdAndUser_Id(billId, userId))
                .thenReturn(Optional.empty());

        assertThrows(BillNotFoundException.class, () -> {
            service.execute(billId, user);
        });

        verify(billRepository, times(1)).findByIdAndUser_Id(billId, userId);
    }

    @Test
    public void shouldCorrectlyMapEntityToDTO() throws BillNotFoundException {
        final User user = new User();
        user.setId(userId);

        final LocalDate dueDate = LocalDate.of(2025, 2, 15);

        final Bill bill = new Bill(billId, dueDate, 250.0, "Tesst Description", user);

        when(billRepository.findByIdAndUser_Id(billId, userId))
                .thenReturn(Optional.of(bill));

        final BillDTO result = service.execute(billId, user);

        assertNotNull(result);
        assertEquals(bill.getId(), result.getId());
        assertEquals(bill.getDescription(), result.getDescription());
        assertEquals(bill.getAmount(), result.getAmount());
        assertEquals(bill.getDueDate(), result.getDueDate());
        assertEquals(bill.getStatus(), result.getStatus());
    }

}
