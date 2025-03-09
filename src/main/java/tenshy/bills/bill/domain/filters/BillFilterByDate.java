package tenshy.bills.bill.domain.filters;

import tenshy.bills.bill.domain.enums.BillStatus;
import tenshy.bills.shared.Filter;

import java.time.LocalDate;

public interface BillFilterByDate extends Filter {

    void setDueDate(LocalDate dueDate);

    LocalDate getDueDate();

    void setDescription(String description);

    String getDescription();

    void setStatus(BillStatus status);
    BillStatus getStatus();
}
