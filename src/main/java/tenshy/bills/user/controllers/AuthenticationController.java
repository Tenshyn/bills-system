package tenshy.bills.user.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tenshy.bills.user.application.dtos.AuthenticationDTO;
import tenshy.bills.user.application.dtos.LoginResponseDTO;
import tenshy.bills.user.application.dtos.RegisterUserDTO;
import tenshy.bills.user.application.exceptions.IncorrectLoginInformationException;
import tenshy.bills.user.application.exceptions.UserAlreadyExistsWithEmailException;
import tenshy.bills.user.application.services.ILoginUserService;
import tenshy.bills.user.application.services.IRegisterUserService;

@RestController
public class AuthenticationController implements AuthenticationApi {

    private final ILoginUserService loginUserService;
    private final IRegisterUserService registerUserService;

    @Autowired
    public AuthenticationController(final ILoginUserService loginUserService,
                                    final IRegisterUserService registerUserService) {
        this.loginUserService = loginUserService;
        this.registerUserService = registerUserService;
    }

    @Override
    public ResponseEntity<Void> registerUser(@RequestBody @Valid RegisterUserDTO registerDTO) throws UserAlreadyExistsWithEmailException {
        registerUserService.execute(registerDTO);

        return ResponseEntity.status(201).build();
    }

    @Override
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody @Valid AuthenticationDTO authenticationDTO) throws IncorrectLoginInformationException {
        final String token = loginUserService.execute(authenticationDTO);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
