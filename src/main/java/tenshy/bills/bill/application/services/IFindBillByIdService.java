package tenshy.bills.bill.application.services;

import tenshy.bills.bill.application.dtos.BillDTO;
import tenshy.bills.bill.application.exceptions.BillNotFoundException;
import tenshy.bills.user.domain.models.User;

import java.util.UUID;

public interface IFindBillByIdService {

    BillDTO execute(UUID billId, User user) throws BillNotFoundException;

}
