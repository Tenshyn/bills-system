package tenshy.bills.user.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tenshy.bills.infrastructure.security.TokenService;
import tenshy.bills.user.application.dtos.AuthenticationDTO;
import tenshy.bills.user.application.exceptions.IncorrectLoginInformationException;
import tenshy.bills.user.application.services.impl.LoginUserService;
import tenshy.bills.user.domain.models.User;
import tenshy.bills.user.domain.repositories.IUserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginUserServiceTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginUserService loginUserService;

    private User testUser;
    private AuthenticationDTO validAuthDTO;
    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encodedPassword123";
    private static final String SAMPLE_TOKEN = "sample.jwt.token";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail(VALID_EMAIL);
        testUser.setPassword(ENCODED_PASSWORD);
        testUser.setName("Test User");

        validAuthDTO = new AuthenticationDTO(VALID_EMAIL, VALID_PASSWORD);
    }

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() throws IncorrectLoginInformationException {
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(VALID_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(tokenService.generateToken(testUser)).thenReturn(SAMPLE_TOKEN);

        final String result = loginUserService.execute(validAuthDTO);

        assertEquals(SAMPLE_TOKEN, result);
        verify(userRepository).findByEmail(VALID_EMAIL);
        verify(passwordEncoder).matches(VALID_PASSWORD, ENCODED_PASSWORD);
        verify(tokenService).generateToken(testUser);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(IncorrectLoginInformationException.class, () ->
                loginUserService.execute(validAuthDTO));

        verify(userRepository).findByEmail(VALID_EMAIL);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(tokenService, never()).generateToken(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(VALID_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(IncorrectLoginInformationException.class, () ->
                loginUserService.execute(validAuthDTO));

        verify(userRepository).findByEmail(VALID_EMAIL);
        verify(passwordEncoder).matches(VALID_PASSWORD, ENCODED_PASSWORD);
        verify(tokenService, never()).generateToken(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        final AuthenticationDTO authDTOWithNullPassword = new AuthenticationDTO(VALID_EMAIL, null);
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(testUser));

        when(passwordEncoder.matches(null, ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(IncorrectLoginInformationException.class, () ->
                loginUserService.execute(authDTOWithNullPassword));

        verify(userRepository).findByEmail(VALID_EMAIL);
        verify(tokenService, never()).generateToken(any(User.class));
    }

    @Test
    void shouldVerifyTokenServiceIsCalledWithCorrectUser() throws IncorrectLoginInformationException {
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(VALID_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(tokenService.generateToken(testUser)).thenReturn(SAMPLE_TOKEN);

        loginUserService.execute(validAuthDTO);

        verify(tokenService).generateToken(testUser);
    }

}
