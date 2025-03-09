package tenshy.bills.bill.application.services;

import org.springframework.web.multipart.MultipartFile;
import tenshy.bills.bill.application.exceptions.InvalidCSVFormatException;
import tenshy.bills.user.domain.models.User;

public interface IImportBillFromCSVService {

    void execute(MultipartFile file, User user) throws InvalidCSVFormatException;

}
