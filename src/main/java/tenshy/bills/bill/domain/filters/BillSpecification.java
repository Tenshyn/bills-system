package tenshy.bills.bill.domain.filters;

import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import tenshy.bills.bill.domain.models.Bill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static tenshy.bills.shared.Utils.isNullOrEmpty;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BillSpecification {

    public static Specification<Bill> getBillsByFilter(final BillFilter filter, final UUID userId) {
        return (root, query, criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            if (nonNull(userId)) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }

            if (nonNull(filter.getDueDate())) {
                predicates.add(criteriaBuilder.equal(root.get("dueDate"), filter.getDueDate()));
            }

            if (nonNull(filter.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            }

            if (!isNullOrEmpty(filter.getDescription())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                        "%" + filter.getDescription().toLowerCase() + "%"));
            }

            if (filter.getStartDueDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), filter.getStartDueDate()));
            }

            if (nonNull(filter.getEndDueDate())) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), filter.getEndDueDate()));
            }

            if (nonNull(filter.getMinAmount())) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), filter.getMinAmount()));
            }

            if (nonNull(filter.getMaxAmount())) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), filter.getMaxAmount()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
