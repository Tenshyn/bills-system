package tenshy.bills.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static LocalDate parseDate(String date) throws Exception {
        try {
            if(isNullOrEmpty(date)) return null;

            return LocalDate.parse(date, formatter);
        } catch (Exception e) {
            throw new Exception("Invalid date format: " + date);
        }
    }

    public static boolean isNullOrEmpty(String string) {
        return isNull(string) || string.isEmpty();
    }

}
