package tenshy.bills.bill.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tenshy.bills.bill.domain.enums.BillStatus;

import tenshy.bills.bill.domain.models.Bill;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface IBillRepository extends JpaRepository<Bill, UUID>, JpaSpecificationExecutor<Bill> {

    Optional<Bill> findByIdAndUser_Id(UUID id, UUID userId);

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Bill b WHERE b.status = :status AND b.paymentDate BETWEEN :startDate AND :endDate AND b.userId = :userId")
    Double findTotalPaidAmountByPeriod(@Param("status") BillStatus status, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("userId") UUID userId);
}
