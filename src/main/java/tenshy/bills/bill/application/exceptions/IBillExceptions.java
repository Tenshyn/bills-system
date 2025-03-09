package tenshy.bills.bill.application.exceptions;

public interface IBillExceptions {

    String BILL_NOT_FOUND_EXCEPTION_MSG = "Bill not found with the informed Id";
    BillNotFoundException BILL_NOT_FOUND_EXCEPTION = new BillNotFoundException(BILL_NOT_FOUND_EXCEPTION_MSG);

    String BILLS_NOT_FOUND_EXCEPTION_MSG = "No bill were found with the informed filter";
    BillNotFoundException BILLS_NOT_FOUND_EXCEPTION = new BillNotFoundException(BILLS_NOT_FOUND_EXCEPTION_MSG);

}
