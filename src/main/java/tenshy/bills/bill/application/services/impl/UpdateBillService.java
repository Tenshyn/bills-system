package tenshy.bills.bill.application.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tenshy.bills.bill.application.dtos.BillDTO;
import tenshy.bills.bill.application.dtos.SaveBillDTO;
import tenshy.bills.bill.application.dtos.UpdateBillDTO;
import tenshy.bills.bill.application.exceptions.BillNotFoundException;
import tenshy.bills.bill.application.mappers.BillMapper;
import tenshy.bills.bill.application.services.ISaveBillService;
import tenshy.bills.bill.application.services.IUpdateBillService;
import tenshy.bills.bill.domain.models.Bill;
import tenshy.bills.bill.domain.repositories.IBillRepository;
import tenshy.bills.user.domain.models.User;

import java.time.LocalDate;

import static java.util.Objects.nonNull;
import static tenshy.bills.bill.application.exceptions.IBillExceptions.BILL_NOT_FOUND_EXCEPTION;

@Service
public class UpdateBillService implements IUpdateBillService {

    private final IBillRepository billRepository;

    @Autowired
    public UpdateBillService(final IBillRepository repository) {
        this.billRepository = repository;
    }

    @Override
    public BillDTO execute(final UpdateBillDTO updateBillDTO, final User user) throws BillNotFoundException {
        final Bill bill = billRepository.findByIdAndUser_Id(updateBillDTO.id(), user.getId())
                .orElseThrow(() -> BILL_NOT_FOUND_EXCEPTION);

        if(nonNull(updateBillDTO.dueDate())) bill.setDueDate(updateBillDTO.dueDate());
        if(nonNull(updateBillDTO.status())) bill.setStatus(updateBillDTO.status());

        bill.setPaymentDate(updateBillDTO.paymentDate());
        bill.setDescription(updateBillDTO.description());

        final Bill savedBill = billRepository.save(bill);
        return BillMapper.dtoFromEntity(savedBill);
    }
}
