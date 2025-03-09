package tenshy.bills.bill.domain.filters;

import tenshy.bills.bill.domain.enums.BillStatus;
import tenshy.bills.shared.Filter;

import java.time.LocalDate;
import java.util.Date;

public interface BillFilterByPeriod extends Filter {

    void setStartDate(LocalDate  startDate);
    LocalDate getStartDate();

    void setEndDate(LocalDate  endDate);
    LocalDate  getEndDate();

    void setStatus(BillStatus status);
    BillStatus getStatus();
}
