package tenshy.bills.user.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tenshy.bills.user.domain.models.User;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

}
