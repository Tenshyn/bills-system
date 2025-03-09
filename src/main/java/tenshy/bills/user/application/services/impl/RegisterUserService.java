package tenshy.bills.user.application.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tenshy.bills.user.application.dtos.RegisterUserDTO;
import tenshy.bills.user.application.exceptions.UserAlreadyExistsWithEmailException;
import tenshy.bills.user.application.services.IRegisterUserService;
import tenshy.bills.user.domain.models.User;
import tenshy.bills.user.domain.repositories.IUserRepository;

import java.util.Optional;

import static tenshy.bills.user.application.exceptions.IUserExceptions.USER_ALREADY_EXISTS_WITH_EMAIL_EXCEPTION;

@Service
public class RegisterUserService implements IRegisterUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegisterUserService(final IUserRepository userRepository,
                               final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void execute(final RegisterUserDTO registerUserDTO) throws UserAlreadyExistsWithEmailException {
        final Optional<User> userFound = userRepository.findByEmail(registerUserDTO.email().trim());

        if (userFound.isPresent()) {
            throw USER_ALREADY_EXISTS_WITH_EMAIL_EXCEPTION;
        }

        final User user = new User();
        user.setName(registerUserDTO.name());
        user.setEmail(registerUserDTO.email());
        user.setPassword(passwordEncoder.encode(registerUserDTO.password()));

        userRepository.save(user);
    }
}
