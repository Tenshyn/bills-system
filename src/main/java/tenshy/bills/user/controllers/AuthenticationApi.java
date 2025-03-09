package tenshy.bills.user.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import tenshy.bills.user.application.dtos.AuthenticationDTO;
import tenshy.bills.user.application.dtos.LoginResponseDTO;
import tenshy.bills.user.application.dtos.RegisterUserDTO;
import tenshy.bills.user.application.exceptions.IncorrectLoginInformationException;
import tenshy.bills.user.application.exceptions.UserAlreadyExistsWithEmailException;
import tenshy.bills.shared.presentation.responses.ErrorResponse;

import static tenshy.bills.user.application.exceptions.IUserExceptions.INVALID_LOGIN_INFORMATION_EXCEPTION_MSG;
import static tenshy.bills.user.application.exceptions.IUserExceptions.USER_ALREADY_EXISTS_WITH_EMAIL_EXCEPTION_MSG;

@RequestMapping("/auth")
public interface AuthenticationApi {

    @Operation(summary = "Create a new user", description = "Register a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterUserDTO.class))),
            @ApiResponse(responseCode = "400", description = USER_ALREADY_EXISTS_WITH_EMAIL_EXCEPTION_MSG,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<Void> registerUser(RegisterUserDTO registerDTO) throws UserAlreadyExistsWithEmailException;

    @Operation(summary = "Login", description = "Validate login information and returns authenticated token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = INVALID_LOGIN_INFORMATION_EXCEPTION_MSG,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<LoginResponseDTO> loginUser(AuthenticationDTO authenticationDTO) throws IncorrectLoginInformationException;

}
