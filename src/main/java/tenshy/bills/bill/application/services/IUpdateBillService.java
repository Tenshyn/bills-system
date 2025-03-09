package tenshy.bills.bill.application.services;

import tenshy.bills.bill.application.dtos.BillDTO;
import tenshy.bills.bill.application.dtos.UpdateBillDTO;
import tenshy.bills.bill.application.exceptions.BillNotFoundException;
import tenshy.bills.user.domain.models.User;

public interface IUpdateBillService {

    BillDTO execute(UpdateBillDTO updateBillDTO, User user) throws BillNotFoundException;

}
