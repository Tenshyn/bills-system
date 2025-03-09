package tenshy.bills.user.application.dtos;

import jakarta.validation.constraints.NotNull;

public record AuthenticationDTO(@NotNull String email, String password) {
}
