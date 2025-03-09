package tenshy.bills.shared.presentation.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Schema(description = "Standard error response structure")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Error message", example = "Email address already in use")
    private String message;

    @Schema(description = "Exception class name", example = "tenshy.bills.exceptions.UserAlreadyExistsWithEmailException")
    private String exception;
}
