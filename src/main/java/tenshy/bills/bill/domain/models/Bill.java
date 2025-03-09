package tenshy.bills.bill.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tenshy.bills.bill.domain.enums.BillStatus;
import tenshy.bills.bill.domain.exceptions.AlreadyPaidBillException;
import tenshy.bills.user.domain.models.User;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Bill {

    @Id
    @GeneratedValue(generator = "uuid4")
    @Column(columnDefinition = "UUID")
    private UUID id;
    private LocalDate  dueDate;
    private LocalDate paymentDate;
    private Double amount;
    private String description;
    @Enumerated(EnumType.STRING)
    private BillStatus status;
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public void setUser(final User user) {
        this.userId = user.getId();
        this.user = user;
    }

    public void pay() throws AlreadyPaidBillException {
        if(this.status.equals(BillStatus.PAID)) {
            throw new AlreadyPaidBillException("Cannot pay an already paid bill");
        }

        final LocalDate  currentDate = LocalDate.now();
        boolean isOverdue = currentDate.isAfter(this.getDueDate());
        BillStatus newStatus = isOverdue ? BillStatus.LATE_PAYMENT : BillStatus.PAID;
        this.setStatus(newStatus);
        this.setPaymentDate(currentDate);
    }

    public Bill() {
        this.status = BillStatus.PENDING;
    }

    public Bill(final UUID id, final LocalDate dueDate, final Double amount, final String description, final User user) {
        this.dueDate = dueDate;
        this.id = id;
        this.amount = amount;
        this.status = BillStatus.PENDING;
        this.description = description;
        this.setUser(user);
    }
}
