package tenshy.bills.bill.application.services;

import tenshy.bills.bill.application.dtos.BillDTO;
import tenshy.bills.bill.application.dtos.SaveBillDTO;
import tenshy.bills.user.domain.models.User;

public interface ISaveBillService {

    BillDTO execute(SaveBillDTO saveBillDTO, User user);

}
