package tenshy.bills.user.application.exceptions;

public interface IUserExceptions {

    String USER_ALREADY_EXISTS_WITH_EMAIL_EXCEPTION_MSG = "Email address already in use";
    UserAlreadyExistsWithEmailException USER_ALREADY_EXISTS_WITH_EMAIL_EXCEPTION = new UserAlreadyExistsWithEmailException(USER_ALREADY_EXISTS_WITH_EMAIL_EXCEPTION_MSG);

    String INVALID_LOGIN_INFORMATION_EXCEPTION_MSG = "Incorrect email or password";
    IncorrectLoginInformationException INVALID_LOGIN_INFORMATION_EXCEPTION = new IncorrectLoginInformationException(INVALID_LOGIN_INFORMATION_EXCEPTION_MSG);


}
