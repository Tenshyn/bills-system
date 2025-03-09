package tenshy.bills.bill.application.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tenshy.bills.bill.application.dtos.BillDTO;
import tenshy.bills.bill.application.exceptions.BillNotFoundException;
import tenshy.bills.bill.application.mappers.BillMapper;
import tenshy.bills.bill.application.services.IFindBillsByFilterService;
import tenshy.bills.bill.domain.filters.BillFilter;
import tenshy.bills.bill.domain.filters.BillSpecification;
import tenshy.bills.bill.domain.models.Bill;
import tenshy.bills.bill.domain.repositories.IBillRepository;
import tenshy.bills.shared.application.PageableList;
import tenshy.bills.user.domain.models.User;

import java.util.List;

import static tenshy.bills.bill.application.exceptions.IBillExceptions.BILLS_NOT_FOUND_EXCEPTION;

@Service
public class FindBillsByFilterService implements IFindBillsByFilterService {

    private final IBillRepository billRepository;

    @Autowired
    public FindBillsByFilterService(final IBillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Override
    public PageableList<BillDTO> execute(final BillFilter filter, final User user) throws BillNotFoundException {
        final Page<Bill> bills = billRepository.findAll(BillSpecification.getBillsByFilter(filter, user.getId()), filter.getPageable());

        if (bills.isEmpty()) throw BILLS_NOT_FOUND_EXCEPTION;

        final List<BillDTO> billDTOS = bills.map(BillMapper::dtoFromEntity).stream().toList();

        return PageableList.from(billDTOS, filter.getPage(), bills.getTotalPages(), bills.getTotalElements());
    }
}
