package tenshy.bills.bill.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tenshy.bills.bill.application.dtos.BillDTO;
import tenshy.bills.bill.application.dtos.SaveBillDTO;
import tenshy.bills.bill.application.dtos.UpdateBillDTO;
import tenshy.bills.bill.application.exceptions.BillNotFoundException;
import tenshy.bills.bill.application.exceptions.InvalidCSVFormatException;
import tenshy.bills.bill.application.filters.BillSpecificationFilter;
import tenshy.bills.bill.application.filters.PaidBillPeriodFilter;
import tenshy.bills.bill.application.filters.PendingBillFilter;
import tenshy.bills.bill.application.services.*;
import tenshy.bills.bill.controllers.responses.BillPageableResponse;
import tenshy.bills.bill.domain.exceptions.AlreadyPaidBillException;
import tenshy.bills.shared.application.PageableList;
import tenshy.bills.user.domain.models.User;

import java.util.List;
import java.util.UUID;

@RestController
public class BillController implements BillApi {

    private final IFindAmountPaidInPeriodService findAmountPaidInPeriodService;
    private final IImportBillFromCSVService importBillFromCSVService;
    private final IFindBillsByFilterService findBillsByFilterService;
    private final IFindBillByIdService findBillByIdService;
    private final IUpdateBillService updateBillService;
    private final ISaveBillService saveBillService;
    private final IPayBillService payBillService;

    @Autowired
    public BillController(final IFindAmountPaidInPeriodService findAmountPaidInPeriodService,
                          final IImportBillFromCSVService importBillFromCSVService,
                          final IFindBillsByFilterService findBillsByFilterService,
                          final IFindBillByIdService findBillByIdService,
                          final IUpdateBillService updateBillService,
                          final ISaveBillService saveBillService,
                          final IPayBillService payBillService) {
        this.findAmountPaidInPeriodService = findAmountPaidInPeriodService;
        this.importBillFromCSVService = importBillFromCSVService;
        this.findBillsByFilterService = findBillsByFilterService;
        this.findBillByIdService = findBillByIdService;
        this.updateBillService = updateBillService;
        this.saveBillService = saveBillService;
        this.payBillService = payBillService;
    }

    @Override
    public ResponseEntity<BillDTO> createBill(@RequestBody SaveBillDTO saveBillDTO, Authentication authentication) {
        final User currentUser = (User) authentication.getPrincipal();
        final BillDTO newBill = this.saveBillService.execute(saveBillDTO, currentUser);

        return ResponseEntity.status(201).body(newBill);
    }

    @Override
    public ResponseEntity<BillDTO> updateBill(@RequestBody UpdateBillDTO updateBillDTO, Authentication authentication) throws BillNotFoundException {
        final User currentUser = (User) authentication.getPrincipal();
        final BillDTO updatedBill = this.updateBillService.execute(updateBillDTO, currentUser);

        return ResponseEntity.ok(updatedBill);
    }

    @Override
    public ResponseEntity<Void> payBill(@PathVariable UUID billId, Authentication authentication) throws BillNotFoundException, AlreadyPaidBillException {
        final User currentUser = (User) authentication.getPrincipal();
        payBillService.execute(billId, currentUser);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<BillPageableResponse> findPendingBy(@ModelAttribute PendingBillFilter filter, Authentication authentication) throws BillNotFoundException {
        final User currentUser = (User) authentication.getPrincipal();
        final BillSpecificationFilter specificationFilter = BillSpecificationFilter.from(filter);
        final PageableList<BillDTO> bills = this.findBillsByFilterService.execute(specificationFilter, currentUser);

        return ResponseEntity.ok(BillPageableResponse.from(bills.getItems(), bills.getPage(), bills.getTotalPages(), bills.getTotalItems()));
    }

    @Override
    public ResponseEntity<BillPageableResponse> findBy(@Valid BillSpecificationFilter filter, Authentication authentication) throws BillNotFoundException {
        final User currentUser = (User) authentication.getPrincipal();
        final PageableList<BillDTO> bills = this.findBillsByFilterService.execute(filter, currentUser);

        return ResponseEntity.ok(BillPageableResponse.from(bills.getItems(), bills.getPage(), bills.getTotalPages(), bills.getTotalItems()));
    }

    @Override
    public ResponseEntity<BillDTO> findBillBy(@PathVariable UUID billId, Authentication authentication) throws BillNotFoundException {
        final User currentUser = (User) authentication.getPrincipal();
        final BillDTO bill = this.findBillByIdService.execute(billId, currentUser);

        return ResponseEntity.ok(bill);
    }

    @Override
    public ResponseEntity<Double> findPaidAmountByPeriod(@ModelAttribute PaidBillPeriodFilter filter, Authentication authentication) {
        final User currentUser = (User) authentication.getPrincipal();

        final Double amount = findAmountPaidInPeriodService.execute(filter, currentUser);
        return ResponseEntity.ok(amount);
    }

    @Override
    public ResponseEntity<Void> importBills(@RequestParam("file") MultipartFile file, Authentication authentication) throws InvalidCSVFormatException {
        final User currentUser = (User) authentication.getPrincipal();
        this.importBillFromCSVService.execute(file, currentUser);

        return ResponseEntity.status(201).build();
    }
}
