package tenshy.bills.bill.application.services;

import tenshy.bills.bill.application.dtos.BillDTO;
import tenshy.bills.bill.application.exceptions.BillNotFoundException;
import tenshy.bills.bill.domain.filters.BillFilter;
import tenshy.bills.shared.application.PageableList;
import tenshy.bills.user.domain.models.User;

public interface IFindBillsByFilterService {

    PageableList<BillDTO> execute(BillFilter filter, User user) throws BillNotFoundException;

}
