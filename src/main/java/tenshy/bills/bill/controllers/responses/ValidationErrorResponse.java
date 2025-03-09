package tenshy.bills.bill.controllers.responses;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ValidationErrorResponse {

    private final Map<String, String> errors = new HashMap<>();

    public void addError(String field, String message) {
        errors.put(field, message);
    }

}
