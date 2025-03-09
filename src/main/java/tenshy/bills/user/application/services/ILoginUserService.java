package tenshy.bills.user.application.services;

import tenshy.bills.user.application.dtos.AuthenticationDTO;
import tenshy.bills.user.application.exceptions.IncorrectLoginInformationException;

public interface ILoginUserService {

    String execute(AuthenticationDTO authenticationDTO) throws IncorrectLoginInformationException;

}
