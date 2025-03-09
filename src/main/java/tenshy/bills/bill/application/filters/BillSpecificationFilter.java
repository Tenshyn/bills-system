package tenshy.bills.bill.application.filters;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import tenshy.bills.bill.domain.enums.BillStatus;
import tenshy.bills.bill.domain.filters.BillFilter;

import java.time.LocalDate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class BillSpecificationFilter implements BillFilter {

    private String description;
    private BillStatus status;
    private LocalDate dueDate;
    private LocalDate startDueDate;
    private LocalDate endDueDate;
    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum amount cannot be negative")
    private Double minAmount;
    @DecimalMin(value = "0.0", inclusive = true, message = "Maximum amount cannot be negative")
    private Double maxAmount;
    private int page = 0;

    @Override
    public void setStartDueDate(LocalDate startDueDate) {
        this.startDueDate = startDueDate;
    }

    @Override
    public LocalDate getStartDueDate() {
        return this.startDueDate;
    }

    @Override
    public void setEndDueDate(LocalDate endDueDate) {
        this.endDueDate = endDueDate;
    }

    @Override
    public LocalDate getEndDueDate() {
        return this.endDueDate;
    }

    @Override
    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Override
    public Double getMaxAmount() {
        return this.maxAmount;
    }

    @Override
    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }

    @Override
    public Double getMinAmount() {
        return this.minAmount;
    }

    @Override
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;

        if(isNull(dueDate)) return;

        this.startDueDate = null;
        this.endDueDate = null;
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
    public void setStatus(BillStatus status) {
        this.status = status;
    }

    @Override
    public BillStatus getStatus() {
        return this.status;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getPage() {
        return this.page;
    }

    public static BillSpecificationFilter from(PendingBillFilter pendingBillFilter) {
        final BillSpecificationFilter filter = new BillSpecificationFilter();

        filter.setDueDate(pendingBillFilter.getDueDate());
        filter.setDescription(pendingBillFilter.getDescription());
        filter.setStatus(pendingBillFilter.getStatus());
        filter.setPage(pendingBillFilter.getPage());

        return filter;
    }

    @AssertTrue(message = "End due date must be after start due date")
    private boolean isDateRangeValid() {
        if (isNull(startDueDate) || isNull(endDueDate
        )) {
            return true;
        }
        return endDueDate.isEqual(startDueDate) || endDueDate.isAfter(startDueDate);
    }

    @AssertTrue(message = "Maximum amount must be greater than or equal to minimum amount")
    private boolean isAmountRangeValid() {
        if (minAmount == null || maxAmount == null) {
            return true;
        }
        return maxAmount >= minAmount;
    }

    @AssertTrue(message = "Cannot specify both exact due date and a date range")
    private boolean isDateUsageValid() {
        if (nonNull(dueDate)) {
            return startDueDate == null && endDueDate == null;
        }
        return true;
    }

}
