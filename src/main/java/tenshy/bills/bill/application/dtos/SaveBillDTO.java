package tenshy.bills.bill.application.dtos;

import java.time.LocalDate;

public record SaveBillDTO(LocalDate dueDate, double amount, String description) {
}
