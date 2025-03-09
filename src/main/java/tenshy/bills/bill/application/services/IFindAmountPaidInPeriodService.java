package tenshy.bills.bill.application.services;

import tenshy.bills.bill.application.exceptions.BillNotFoundException;
import tenshy.bills.bill.application.filters.PaidBillPeriodFilter;
import tenshy.bills.user.domain.models.User;

public interface IFindAmountPaidInPeriodService {

    Double execute(PaidBillPeriodFilter filter, User user);

}
