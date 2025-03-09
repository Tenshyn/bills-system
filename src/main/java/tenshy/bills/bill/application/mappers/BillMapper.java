package tenshy.bills.bill.application.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tenshy.bills.bill.application.dtos.BillDTO;
import tenshy.bills.bill.application.dtos.SaveBillDTO;
import tenshy.bills.bill.domain.enums.BillStatus;
import tenshy.bills.bill.domain.models.Bill;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BillMapper {

    public static BillDTO dtoFromEntity(final Bill bill) {
        final BillDTO billDTO = new BillDTO();
        billDTO.setId(bill.getId());
        billDTO.setDueDate(bill.getDueDate());
        billDTO.setPaymentDate(bill.getPaymentDate());
        billDTO.setAmount(bill.getAmount());
        billDTO.setDescription(bill.getDescription());
        billDTO.setStatus(bill.getStatus());
        return billDTO;
    }

    public static Bill entityFromDto(final SaveBillDTO dto) {
        final Bill bill = new Bill();
        bill.setDueDate(dto.dueDate());
        bill.setAmount(dto.amount());
        bill.setDescription(dto.description());
        bill.setStatus(BillStatus.PENDING);

        return bill;
    }

}
