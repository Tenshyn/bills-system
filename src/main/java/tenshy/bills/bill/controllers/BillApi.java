package tenshy.bills.bill.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
import tenshy.bills.bill.controllers.responses.BillPageableResponse;
import tenshy.bills.bill.controllers.responses.ValidationErrorResponse;
import tenshy.bills.bill.domain.exceptions.AlreadyPaidBillException;
import tenshy.bills.shared.presentation.responses.ErrorResponse;

import java.util.UUID;

import static tenshy.bills.bill.application.exceptions.IBillExceptions.BILLS_NOT_FOUND_EXCEPTION_MSG;
import static tenshy.bills.bill.application.exceptions.IBillExceptions.BILL_NOT_FOUND_EXCEPTION_MSG;

@RequestMapping("/bills")
public interface BillApi {

    @Operation(summary = "Save a new bill", description = "Create a new bill in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bill successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BillDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<BillDTO> createBill(@Valid SaveBillDTO saveBillDTO, Authentication authentication);

    @Operation(summary = "Update existing bill", description = "Update bill with the parameters informed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bill successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BillDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = BILLS_NOT_FOUND_EXCEPTION_MSG,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BillDTO> updateBill(@Valid UpdateBillDTO saveBillDTO, Authentication authentication) throws BillNotFoundException;


    @Operation(summary = "Pay bill", description = "Pay existing bill in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bill paid successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = BILL_NOT_FOUND_EXCEPTION_MSG,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/{billId}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Void> payBill(UUID billId, Authentication authentication) throws BillNotFoundException, AlreadyPaidBillException;

    @Operation(summary = "Retrieves bill list by filter", description = "Retrieves users bills according to filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bills found successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BillPageableResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = BILLS_NOT_FOUND_EXCEPTION_MSG,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/pending")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BillPageableResponse> findPendingBy(PendingBillFilter filter, Authentication authentication) throws BillNotFoundException;

    @Operation(summary = "Retrieves bill list by filter", description = "Retrieves users bills according to filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bills found successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BillPageableResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters",
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = BILLS_NOT_FOUND_EXCEPTION_MSG,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BillPageableResponse> findBy(BillSpecificationFilter filter, Authentication authentication) throws BillNotFoundException;


    @Operation(summary = "Retrieves bill by id", description = "Retrieves user bill by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bill found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BillDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = BILL_NOT_FOUND_EXCEPTION_MSG,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{billId}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BillDTO> findBillBy(UUID billId, Authentication authentication) throws BillNotFoundException;

    @Operation(summary = "Retrieves amount paid", description = "Retrieves the total amount user paid in period informed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Amount found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/paid")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Double> findPaidAmountByPeriod(PaidBillPeriodFilter filter, Authentication authentication);

    @Operation(summary = "Import bills", description = "Import bills from CSV file with headers: dueDate, paymentDate, amount, description, status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bills imported successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid CSV format",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(value = "import", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<Void> importBills(MultipartFile file, Authentication authentication) throws InvalidCSVFormatException;
}
