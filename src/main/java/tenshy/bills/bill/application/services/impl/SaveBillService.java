package tenshy.bills.bill.application.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tenshy.bills.bill.application.dtos.BillDTO;
import tenshy.bills.bill.application.dtos.SaveBillDTO;
import tenshy.bills.bill.application.mappers.BillMapper;
import tenshy.bills.bill.application.services.ISaveBillService;
import tenshy.bills.bill.domain.models.Bill;
import tenshy.bills.bill.domain.repositories.IBillRepository;
import tenshy.bills.user.domain.models.User;

@Service
public class SaveBillService implements ISaveBillService {

    private final IBillRepository billRepository;

    @Autowired
    public SaveBillService(final IBillRepository repository) {
        this.billRepository = repository;
    }

    @Override
    public BillDTO execute(SaveBillDTO saveBillDTO, User user) {
        final Bill bill = BillMapper.entityFromDto(saveBillDTO);
        bill.setUser(user);
        final Bill savedBill = billRepository.save(bill);
        return BillMapper.dtoFromEntity(savedBill);
    }
}
