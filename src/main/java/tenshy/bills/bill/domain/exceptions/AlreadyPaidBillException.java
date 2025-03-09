package tenshy.bills.bill.domain.exceptions;

public class AlreadyPaidBillException extends Exception {

    public AlreadyPaidBillException(String message) {
        super(message);
    }

}
