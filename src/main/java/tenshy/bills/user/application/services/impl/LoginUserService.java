package tenshy.bills.user.application.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tenshy.bills.infrastructure.security.TokenService;
import tenshy.bills.user.application.dtos.AuthenticationDTO;
import tenshy.bills.user.application.exceptions.IncorrectLoginInformationException;
import tenshy.bills.user.application.services.ILoginUserService;
import tenshy.bills.user.domain.models.User;
import tenshy.bills.user.domain.repositories.IUserRepository;

import java.util.Optional;

import static tenshy.bills.user.application.exceptions.IUserExceptions.INVALID_LOGIN_INFORMATION_EXCEPTION;

@Service
public class LoginUserService implements ILoginUserService {

    private final TokenService tokenService;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoginUserService(final TokenService tokenService,
                            final IUserRepository userRepository,
                            final PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String execute(final AuthenticationDTO authenticationDTO) throws IncorrectLoginInformationException {
        final User user = userRepository.findByEmail(authenticationDTO.email())
                .orElseThrow(() -> INVALID_LOGIN_INFORMATION_EXCEPTION);

        if (!passwordEncoder.matches(authenticationDTO.password(), user.getPassword())) {
            throw INVALID_LOGIN_INFORMATION_EXCEPTION;
        }
        return tokenService.generateToken(user);
    }
}
