package tenshy.bills.bill.application.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tenshy.bills.bill.application.dtos.BillDTO;
import tenshy.bills.bill.application.exceptions.BillNotFoundException;
import tenshy.bills.bill.application.mappers.BillMapper;
import tenshy.bills.bill.application.services.IFindBillByIdService;
import tenshy.bills.bill.domain.models.Bill;
import tenshy.bills.bill.domain.repositories.IBillRepository;
import tenshy.bills.user.domain.models.User;

import java.util.UUID;

import static tenshy.bills.bill.application.exceptions.IBillExceptions.BILL_NOT_FOUND_EXCEPTION;

@Service
public class FindBillByIdService implements IFindBillByIdService {

    private final IBillRepository billRepository;

    @Autowired
    public FindBillByIdService(final IBillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Override
    public BillDTO execute(final UUID billId, final User user) throws BillNotFoundException {
        final Bill bill = this.billRepository.findByIdAndUser_Id(billId, user.getId())
                .orElseThrow(() -> BILL_NOT_FOUND_EXCEPTION);

        return BillMapper.dtoFromEntity(bill);
    }
}
