package tenshy.bills.bill.application.dtos;

import lombok.Getter;
import lombok.Setter;
import tenshy.bills.bill.domain.enums.BillStatus;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class BillDTO {

    private UUID id;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private Double amount;
    private String description;
    private BillStatus status;



}
