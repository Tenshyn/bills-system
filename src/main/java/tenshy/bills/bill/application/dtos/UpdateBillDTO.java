package tenshy.bills.bill.application.dtos;

import tenshy.bills.bill.domain.enums.BillStatus;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateBillDTO(UUID id, LocalDate dueDate, LocalDate paymentDate, double amount, String description, BillStatus status) {
}
