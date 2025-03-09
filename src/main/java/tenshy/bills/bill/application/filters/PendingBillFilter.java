package tenshy.bills.bill.application.filters;

import io.swagger.v3.oas.annotations.media.Schema;
import tenshy.bills.bill.domain.enums.BillStatus;
import tenshy.bills.bill.domain.filters.BillFilterByDate;

import java.time.LocalDate;

public class PendingBillFilter implements BillFilterByDate {

    private LocalDate dueDate;
    private String description;
    private int page = 0;

    @Override
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public LocalDate getDueDate() {
        return this.dueDate;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setStatus(BillStatus status) {}

    @Override
    @Schema(hidden = true)
    public BillStatus getStatus() {
        return BillStatus.PENDING;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getPage() {
        return this.page;
    }
}
