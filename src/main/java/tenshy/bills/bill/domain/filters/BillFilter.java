package tenshy.bills.bill.domain.filters;

import tenshy.bills.bill.domain.enums.BillStatus;
import tenshy.bills.shared.Filter;
import tenshy.bills.user.domain.models.User;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public interface BillFilter extends Filter {

    void setStartDueDate(LocalDate startDueDate);
    LocalDate getStartDueDate();

    void setEndDueDate(LocalDate endDueDate);
    LocalDate getEndDueDate();

    void setMaxAmount(Double maxAmount);
    Double getMaxAmount();

    void setMinAmount(Double minAmount);
    Double getMinAmount();


    void setDueDate(LocalDate  dueDate);
    LocalDate getDueDate();

    void setDescription(String description);
    String getDescription();

    void setStatus(BillStatus status);
    BillStatus getStatus();

}
