package tenshy.bills.user.application.dtos;

import jakarta.validation.constraints.NotNull;

public record RegisterUserDTO(String name, @NotNull String email, @NotNull String password) {
}
