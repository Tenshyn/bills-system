package tenshy.bills.user.application.exceptions;

public class UserAlreadyExistsWithEmailException extends Exception {

    public UserAlreadyExistsWithEmailException(String message) {
        super(message);
    }

}
