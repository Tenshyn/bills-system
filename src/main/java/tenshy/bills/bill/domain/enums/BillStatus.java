package tenshy.bills.bill.domain.enums;

import static tenshy.bills.shared.Utils.isNullOrEmpty;

public enum BillStatus {

    PENDING, PAID, LATE_PAYMENT, OVERDUE;

    public static BillStatus from(final String value) {
        if(isNullOrEmpty(value)) return null;

        return BillStatus.valueOf(value.toUpperCase());
    }

}
