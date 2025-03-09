package tenshy.bills.bill.application.filters;

import io.swagger.v3.oas.annotations.media.Schema;
import tenshy.bills.bill.domain.enums.BillStatus;
import tenshy.bills.bill.domain.filters.BillFilterByPeriod;

import java.time.LocalDate;

public class PaidBillPeriodFilter implements BillFilterByPeriod {

    private LocalDate  startDate;
    private LocalDate  endDate;
    private int page = 0;

    @Override
    public void setStartDate(LocalDate  startDate) {
        this.startDate = startDate;
    }

    @Override
    public LocalDate getStartDate() {
        return this.startDate;
    }

    @Override
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public LocalDate  getEndDate() {
        return this.endDate;
    }

    @Override
    public void setStatus(BillStatus status) {}

    @Override
    @Schema(hidden = true)
    public BillStatus getStatus() {
        return BillStatus.PAID;
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
