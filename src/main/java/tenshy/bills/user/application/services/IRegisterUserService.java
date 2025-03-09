package tenshy.bills.user.application.services;

import tenshy.bills.user.application.dtos.RegisterUserDTO;
import tenshy.bills.user.application.exceptions.UserAlreadyExistsWithEmailException;

public interface IRegisterUserService {

    void execute(RegisterUserDTO registerUserDTO) throws UserAlreadyExistsWithEmailException;

}
