package tenshy.bills.bill.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tenshy.bills.bill.application.filters.PaidBillPeriodFilter;
import tenshy.bills.bill.application.services.impl.FindAmountPaidInPeriodService;
import tenshy.bills.bill.domain.enums.BillStatus;
import tenshy.bills.bill.domain.repositories.IBillRepository;
import tenshy.bills.user.domain.models.User;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FindAmountPaidInPeriodServiceTest {

    @Mock
    private IBillRepository billRepository;

    private FindAmountPaidInPeriodService service;

    @BeforeEach
    public void setUp() {
        service = new FindAmountPaidInPeriodService(billRepository);
    }

    @Test
    public void shouldReturnCorrectAmountForGivenPeriodAndUser() {
        final PaidBillPeriodFilter filter = new PaidBillPeriodFilter();
        final LocalDate startDate = LocalDate.of(2025, 1, 1);
        final LocalDate endDate = LocalDate.of(2025, 1, 31);
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);

        final User user = new User();
        final UUID userId = UUID.randomUUID();
        user.setId(userId);

        final Double expectedAmount = 1500.50;
        when(billRepository.findTotalPaidAmountByPeriod(BillStatus.PAID, startDate, endDate, userId))
                .thenReturn(expectedAmount);

        final Double actualAmount = service.execute(filter, user);

        assertEquals(expectedAmount, actualAmount);
        verify(billRepository, times(1))
                .findTotalPaidAmountByPeriod(BillStatus.PAID, startDate, endDate, userId);
    }

    @Test
    public void shouldPassCorrectParametersToRepository() {
        final LocalDate startDate = LocalDate.of(2025, 3, 15);
        final LocalDate endDate = LocalDate.of(2025, 4, 15);

        final PaidBillPeriodFilter filter = new PaidBillPeriodFilter();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);

        final User user = new User();
        final UUID userId = UUID.randomUUID();
        user.setId(userId);

        when(billRepository.findTotalPaidAmountByPeriod(BillStatus.PAID, startDate, endDate, userId))
                .thenReturn(0.0);

        service.execute(filter, user);

        verify(billRepository).findTotalPaidAmountByPeriod(BillStatus.PAID, startDate, endDate, userId);
    }

}
