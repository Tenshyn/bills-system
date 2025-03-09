package tenshy.bills.bill.application.services.impl;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tenshy.bills.bill.application.exceptions.InvalidCSVFormatException;
import tenshy.bills.bill.application.services.IImportBillFromCSVService;
import tenshy.bills.bill.domain.enums.BillStatus;
import tenshy.bills.bill.domain.models.Bill;
import tenshy.bills.bill.domain.repositories.IBillRepository;
import tenshy.bills.shared.Utils;
import tenshy.bills.user.domain.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static tenshy.bills.shared.Utils.parseDate;

@Service
public class ImportBillFromCSVService implements IImportBillFromCSVService {

    private final static List<String> EXPECTED_COLUMNS = List.of("dueDate", "paymentDate", "amount", "description", "status");
    private final IBillRepository billRepository;

    @Autowired
    public ImportBillFromCSVService(final IBillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Override
    public void execute(final MultipartFile file, final User user) throws InvalidCSVFormatException {
        if (file == null || file.isEmpty()) {
            throw new InvalidCSVFormatException("Valid csv file is required");
        }

        final List<Bill> bills = new ArrayList<>();

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            reader.mark(1);
            if (reader.read() != 0xFEFF) {
                reader.reset();
            }

            char delimiter = detectDelimiter(file);

            final CSVParser parser = new CSVParserBuilder()
                    .withSeparator(delimiter)
                    .withIgnoreQuotations(false)
                    .build();

            final CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withCSVParser(parser)
                    .withSkipLines(0)
                    .build();

            String[] headers = csvReader.readNext();
            if (headers == null) {
                throw new InvalidCSVFormatException("CSV file must have all required headers");
            }

            validateHeaders(headers);

            String[] values;
            while ((values = csvReader.readNext()) != null) {
                if (values.length != EXPECTED_COLUMNS.size()) {
                    throw new InvalidCSVFormatException(
                            String.format("All rows must have %d columns, row has Is: %d",
                                    EXPECTED_COLUMNS.size(), values.length));
                }

                Bill bill = createBillFromCsvValues(values, user);
                bills.add(bill);
            }
        } catch (NumberFormatException e) {
            throw new InvalidCSVFormatException("Invalid number format: " + e.getMessage());
        } catch (DateTimeParseException e) {
            throw new InvalidCSVFormatException("Invalid date format, expected dd/MM/yyyy: " + e.getMessage());
        } catch (IOException e) {
            throw new InvalidCSVFormatException("Error reading CSV file: " + e.getMessage());
        } catch (InvalidCSVFormatException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidCSVFormatException("Error processing CSV file: " + e.getMessage());
        }

        if (bills.isEmpty()) {
            throw new InvalidCSVFormatException("No valid bills found in CSV file");
        }

        billRepository.saveAll(bills);
    }

    private Bill createBillFromCsvValues(final String[] values, final User user) throws InvalidCSVFormatException {
        try {
            final Bill bill = new Bill();
            bill.setDueDate(Utils.parseDate(values[0]));
            bill.setPaymentDate(Utils.parseDate(values[1]));
            bill.setAmount(parseDouble(values[2]));
            bill.setDescription(values[3]);
            bill.setStatus(BillStatus.from(values[4]));
            bill.setUser(user);
            return bill;
        } catch (Exception e) {
            throw new InvalidCSVFormatException("Error creating bill from CSV row: " + e.getMessage());
        }
    }

    private void validateHeaders(String[] headers) throws InvalidCSVFormatException {
        if (headers.length != EXPECTED_COLUMNS.size()) {
            throw new InvalidCSVFormatException(String.format(
                    "Incorrect number of headers. Expected: %d, Found: %d",
                    EXPECTED_COLUMNS.size(), headers.length));
        }

        for (int i = 0; i < headers.length; i++) {
            String expectedColumn = EXPECTED_COLUMNS.get(i);
            String actualColumn = headers[i].trim();

            if (!expectedColumn.equalsIgnoreCase(actualColumn)) {
                throw new InvalidCSVFormatException(String.format(
                        "Header at position %d should be '%s', but was '%s'",
                        i + 1, expectedColumn, actualColumn));
            }
        }
    }

    private Double parseDouble(String amountStr) {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            throw new NumberFormatException("Amount cannot be empty");
        }

        amountStr = amountStr.trim().replace(",", ".");
        try {
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid amount format: " + amountStr);
        }
    }

    private char detectDelimiter(MultipartFile file) throws IOException, InvalidCSVFormatException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            reader.mark(1000);

            if (reader.read() == 0xFEFF) {
                final String line = reader.readLine();
                reader.reset();
                reader.read();

                return determineDelimiter(line);
            } else {
                reader.reset();
                final String line = reader.readLine();

                return determineDelimiter(line);
            }
        }
    }

    private char determineDelimiter(String line) throws InvalidCSVFormatException {
        if (line == null) {
            throw new InvalidCSVFormatException("CSV file is empty");
        }

        if (line.contains(";")) {
            return ';';
        } else if (line.contains(",")) {
            return ',';
        } else if (line.contains("\t")) {
            return '\t';
        } else {
            throw new InvalidCSVFormatException("Unknown delimiter in CSV file. Supported delimiters: comma, semicolon, tab");
        }
    }

}
