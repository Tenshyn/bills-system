package tenshy.bills.bill.application.services;

import tenshy.bills.bill.application.exceptions.BillNotFoundException;
import tenshy.bills.bill.domain.exceptions.AlreadyPaidBillException;
import tenshy.bills.user.domain.models.User;

import java.util.UUID;

public interface IPayBillService {

    void execute(UUID billId, User user) throws BillNotFoundException, AlreadyPaidBillException;

}
