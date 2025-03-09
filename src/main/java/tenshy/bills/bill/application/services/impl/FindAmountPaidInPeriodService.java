package tenshy.bills.bill.application.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tenshy.bills.bill.application.filters.PaidBillPeriodFilter;
import tenshy.bills.bill.application.services.IFindAmountPaidInPeriodService;
import tenshy.bills.bill.domain.repositories.IBillRepository;
import tenshy.bills.user.domain.models.User;

@Service
public class FindAmountPaidInPeriodService implements IFindAmountPaidInPeriodService {

    private final IBillRepository billRepository;

    @Autowired
    public FindAmountPaidInPeriodService(final IBillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Override
    public Double execute(PaidBillPeriodFilter filter, User user) {

        return billRepository.findTotalPaidAmountByPeriod(filter.getStatus(), filter.getStartDate(), filter.getEndDate(), user.getId());
    }
}
