package tenshy.bills.bill.application.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tenshy.bills.bill.application.exceptions.BillNotFoundException;
import tenshy.bills.bill.application.services.IPayBillService;
import tenshy.bills.bill.domain.exceptions.AlreadyPaidBillException;
import tenshy.bills.bill.domain.repositories.IBillRepository;
import tenshy.bills.user.domain.models.User;
import tenshy.bills.bill.domain.models.Bill;

import java.util.UUID;

import static tenshy.bills.bill.application.exceptions.IBillExceptions.BILL_NOT_FOUND_EXCEPTION;

@Service
public class PayBillService implements IPayBillService {

    private final IBillRepository billRepository;

    @Autowired
    public PayBillService(final IBillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Override
    public void execute(UUID billId, User user) throws BillNotFoundException, AlreadyPaidBillException {
        final Bill bill = billRepository.findByIdAndUser_Id(billId, user.getId()).orElseThrow(() -> BILL_NOT_FOUND_EXCEPTION);
        bill.pay();

        billRepository.save(bill);
    }
}
