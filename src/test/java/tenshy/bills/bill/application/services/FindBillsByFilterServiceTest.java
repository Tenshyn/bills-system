package tenshy.bills.bill.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import tenshy.bills.bill.application.dtos.BillDTO;
import tenshy.bills.bill.application.exceptions.BillNotFoundException;
import tenshy.bills.bill.application.filters.BillSpecificationFilter;
import tenshy.bills.bill.application.services.impl.FindBillsByFilterService;
import tenshy.bills.bill.domain.enums.BillStatus;
import tenshy.bills.bill.domain.filters.BillFilter;
import tenshy.bills.bill.domain.models.Bill;
import tenshy.bills.bill.domain.repositories.IBillRepository;
import tenshy.bills.shared.application.PageableList;
import tenshy.bills.user.domain.models.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FindBillsByFilterServiceTest {

    @Mock
    private IBillRepository billRepository;

    private FindBillsByFilterService service;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        service = new FindBillsByFilterService(billRepository);
    }

    @Test
    public void shouldReturnBillsWhenFound() throws BillNotFoundException {
        final BillFilter filter = new BillSpecificationFilter();
        filter.setStatus(BillStatus.PENDING);
        filter.setPage(0);

        final User user = new User();
        user.setId(userId);

        final List<Bill> billList = createTestBills(user, 3);
        final Page<Bill> billPage = new PageImpl<>(billList, filter.getPageable(), billList.size());

        when(billRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(billPage);

        final PageableList<BillDTO> result = service.execute(filter, user);

        assertNotNull(result);
        assertEquals(3, result.getItems().size());
        assertEquals(0, result.getPage());
        assertEquals(3, result.getTotalItems());
        assertEquals(1, result.getTotalPages());

        verify(billRepository).findAll(any(Specification.class), eq(filter.getPageable()));
    }

    @Test
    public void shouldThrowExceptionWhenNoBillsAreFound() {
        final BillFilter filter = new BillSpecificationFilter();
        filter.setStatus(BillStatus.PAID);

        final User user = new User();
        user.setId(userId);

        final Page<Bill> emptyPage = new PageImpl<>(List.of(), filter.getPageable(), 0);

        when(billRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        assertThrows(BillNotFoundException.class, () -> {
            service.execute(filter, user);
        });

        verify(billRepository).findAll(any(Specification.class), eq(filter.getPageable()));
    }

    private List<Bill> createTestBills(User user, int count) {
        final List<Bill> bills = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Bill bill = new Bill();
            bill.setId(UUID.randomUUID());
            bill.setDescription("Test Bill " + (i + 1));
            bill.setAmount(100.0 * (i + 1));
            bill.setStatus(BillStatus.PENDING);
            bill.setDueDate(LocalDate.now().plusDays(i + 1));
            bill.setUser(user);
            bills.add(bill);
        }
        return bills;
    }

}
