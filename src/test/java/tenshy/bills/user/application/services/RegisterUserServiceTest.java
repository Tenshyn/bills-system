package tenshy.bills.user.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tenshy.bills.user.application.dtos.RegisterUserDTO;
import tenshy.bills.user.application.exceptions.UserAlreadyExistsWithEmailException;
import tenshy.bills.user.application.services.impl.RegisterUserService;
import tenshy.bills.user.domain.models.User;
import tenshy.bills.user.domain.repositories.IUserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterUserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserService registerUserService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private RegisterUserDTO validUserDTO;
    private static final String VALID_NAME = "John Doe";
    private static final String VALID_EMAIL = "john.doe@example.com";
    private static final String VALID_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encodedPassword123";

    @BeforeEach
    void setUp() {
        validUserDTO = new RegisterUserDTO(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
    }

    @Test
    void shouldRegisterUserSuccessfullyWhenUserDoesNotExist() {
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> registerUserService.execute(validUserDTO));

        verify(userRepository).findByEmail(VALID_EMAIL);
        verify(passwordEncoder).encode(VALID_PASSWORD);
        verify(userRepository).save(userCaptor.capture());

        final User savedUser = userCaptor.getValue();
        assertEquals(VALID_NAME, savedUser.getName());
        assertEquals(VALID_EMAIL, savedUser.getEmail());
        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExistsWithEmail() {
        final User existingUser = new User();
        existingUser.setEmail(VALID_EMAIL);

        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsWithEmailException.class, () ->
                registerUserService.execute(validUserDTO));

        verify(userRepository).findByEmail(VALID_EMAIL);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldEncodePasswordCorrectly() throws UserAlreadyExistsWithEmailException {
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        registerUserService.execute(validUserDTO);

        verify(passwordEncoder).encode(VALID_PASSWORD);
        verify(userRepository).save(userCaptor.capture());

        final User savedUser = userCaptor.getValue();
        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());
        assertNotEquals(VALID_PASSWORD, savedUser.getPassword());
    }

    @Test
    void shouldPreserveUserDetailsWhenSaving() throws UserAlreadyExistsWithEmailException {
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        registerUserService.execute(validUserDTO);

        verify(userRepository).save(userCaptor.capture());

        final User savedUser = userCaptor.getValue();
        assertEquals(VALID_NAME, savedUser.getName());
        assertEquals(VALID_EMAIL, savedUser.getEmail());
    }

    @Test
    void shouldCheckEmailCaseSensitivity() {
        final String lowerCaseEmail = "john.doe@example.com";
        final String upperCaseEmail = "JOHN.DOE@EXAMPLE.COM";

        final RegisterUserDTO upperCaseEmailDTO = new RegisterUserDTO(VALID_NAME, upperCaseEmail, VALID_PASSWORD);

        final User existingUser = new User();
        existingUser.setEmail(lowerCaseEmail);

        when(userRepository.findByEmail(upperCaseEmail)).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsWithEmailException.class, () ->
                registerUserService.execute(upperCaseEmailDTO));

        verify(userRepository).findByEmail(upperCaseEmail);
        verify(userRepository, never()).save(any(User.class));
    }
}
