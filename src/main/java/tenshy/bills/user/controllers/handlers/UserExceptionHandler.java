package tenshy.bills.user.controllers.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import tenshy.bills.bill.application.exceptions.BillNotFoundException;
import tenshy.bills.shared.presentation.responses.ErrorResponse;
import tenshy.bills.user.application.exceptions.IncorrectLoginInformationException;
import tenshy.bills.user.application.exceptions.UserAlreadyExistsWithEmailException;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(IncorrectLoginInformationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleInvalidLoginInformationException(
            BillNotFoundException ex, WebRequest request) {

        final ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                "IncorrectLoginInformationException"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistsWithEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsWithEmailExceptionException(
            BillNotFoundException ex, WebRequest request) {

        final ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "UserAlreadyExistsWithEmailException"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
