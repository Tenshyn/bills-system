package tenshy.bills.bill.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import tenshy.bills.bill.application.exceptions.InvalidCSVFormatException;
import tenshy.bills.bill.application.services.impl.ImportBillFromCSVService;
import tenshy.bills.bill.domain.enums.BillStatus;
import tenshy.bills.bill.domain.models.Bill;
import tenshy.bills.bill.domain.repositories.IBillRepository;
import tenshy.bills.user.domain.models.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImportBillFromCSVServiceTest {

    @Mock
    private IBillRepository billRepository;

    @Captor
    private ArgumentCaptor<List<Bill>> billsCaptor;

    private ImportBillFromCSVService service;
    private User testUser;

    @BeforeEach
    public void setUp() {
        service = new ImportBillFromCSVService(billRepository);
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("Tester");
    }

    @Test
    public void shouldImportValidCSVWithCommaDelimiter() throws Exception {
        final String csvContent = "dueDate,paymentDate,amount,description,status\n" +
                "15/01/2025,10/01/2025,100.50,Electricity bill,PAID\n" +
                "01/02/2025,,200.75,Internet bill,PENDING";

        final MultipartFile file = createMockMultipartFile("bills.csv", csvContent);

        when(billRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        service.execute(file, testUser);

        verify(billRepository).saveAll(billsCaptor.capture());
        final List<Bill> capturedBills = billsCaptor.getValue();

        assertEquals(2, capturedBills.size());

        final Bill bill1 = capturedBills.get(0);
        assertEquals(LocalDate.of(2025, 1, 15), bill1.getDueDate());
        assertEquals(LocalDate.of(2025, 1, 10), bill1.getPaymentDate());
        assertEquals(100.50, bill1.getAmount());
        assertEquals("Electricity bill", bill1.getDescription());
        assertEquals(BillStatus.PAID, bill1.getStatus());
        assertEquals(testUser, bill1.getUser());

        final Bill bill2 = capturedBills.get(1);
        assertEquals(LocalDate.of(2025, 2, 1), bill2.getDueDate());
        assertNull(bill2.getPaymentDate());
        assertEquals(200.75, bill2.getAmount());
        assertEquals("Internet bill", bill2.getDescription());
        assertEquals(BillStatus.PENDING, bill2.getStatus());
        assertEquals(testUser, bill2.getUser());
    }

    @Test
    public void shouldImportValidCSVWithSemicolonDelimiter() throws Exception {
        final String csvContent = "dueDate;paymentDate;amount;description;status\n" +
                "15/01/2025;14/01/2025;50.25;Water bill;PAID";

        final MultipartFile file = createMockMultipartFile("bills.csv", csvContent);

        when(billRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        service.execute(file, testUser);

        verify(billRepository).saveAll(billsCaptor.capture());
        final List<Bill> capturedBills = billsCaptor.getValue();

        assertEquals(1, capturedBills.size());

        Bill bill = capturedBills.get(0);
        assertEquals(LocalDate.of(2025, 1, 15), bill.getDueDate());
        assertEquals(LocalDate.of(2025, 1, 14), bill.getPaymentDate());
        assertEquals(50.25, bill.getAmount());
        assertEquals("Water bill", bill.getDescription());
        assertEquals(BillStatus.PAID, bill.getStatus());
    }

    @Test
    public void shouldImportValidCSVWithTabDelimiter() throws Exception {
        String csvContent = "dueDate\tpaymentDate\tamount\tdescription\tstatus\n" +
                "15/01/2025\t10/01/2025\t75.00\tGas bill\tPAID";

        final MultipartFile file = createMockMultipartFile("bills.csv", csvContent);

        when(billRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        service.execute(file, testUser);

        verify(billRepository).saveAll(billsCaptor.capture());
        final List<Bill> capturedBills = billsCaptor.getValue();

        assertEquals(1, capturedBills.size());

        Bill bill = capturedBills.get(0);
        assertEquals(LocalDate.of(2025, 1, 15), bill.getDueDate());
        assertEquals("Gas bill", bill.getDescription());
    }

    @Test
    public void shouldHandleFileWithBOMMarker() throws Exception {
        String csvContent = "\uFEFFdueDate,paymentDate,amount,description,status\n" +
                "15/02/2025,14/02/2025,125.75,Phone bill,PAID";

        final MultipartFile file = createMockMultipartFile("bills.csv", csvContent);

        when(billRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        service.execute(file, testUser);

        verify(billRepository).saveAll(billsCaptor.capture());
        final List<Bill> capturedBills = billsCaptor.getValue();

        assertEquals(1, capturedBills.size());
        assertEquals("Phone bill", capturedBills.get(0).getDescription());
    }

    @Test
    public void shouldThrowExceptionForEmptyFile() {
        final MultipartFile emptyFile = createMockMultipartFile("empty.csv", "");

        assertThrows(InvalidCSVFormatException.class, () -> {
            service.execute(emptyFile, testUser);
        });

        verify(billRepository, never()).saveAll(anyList());
    }

    @Test
    public void shouldThrowExceptionForMissingHeaders() {
        final String csvContent = "date,payment,amount,desc,status\n" +
                "15/01/2025,10/01/2025,100.50,Rent,PAID";

        final MultipartFile file = createMockMultipartFile("wrong_headers.csv", csvContent);

        final InvalidCSVFormatException exception = assertThrows(InvalidCSVFormatException.class, () -> {
            service.execute(file, testUser);
        });

        assertTrue(exception.getMessage().contains("Header"));
        verify(billRepository, never()).saveAll(anyList());
    }

    @Test
    public void shouldThrowExceptionForInvalidDateFormat() {
        final String csvContent = "dueDate,paymentDate,amount,description,status\n" +
                "2025-05-15,2025-05-10,100.50,Electricity bill,PAID";

        final MultipartFile file = createMockMultipartFile("invalid_date.csv", csvContent);

        final InvalidCSVFormatException exception = assertThrows(InvalidCSVFormatException.class, () -> {
            service.execute(file, testUser);
        });

        assertTrue(exception.getMessage().contains("date format"));
        verify(billRepository, never()).saveAll(anyList());
    }

    @Test
    public void shouldThrowExceptionForInvalidAmountFormat() {
        final String csvContent = "dueDate,paymentDate,amount,description,status\n" +
                "15/01/2025, 10/01/2025,abc,Electricity bill,PAID";

        final MultipartFile file = createMockMultipartFile("invalid_amount.csv", csvContent);

        assertThrows(InvalidCSVFormatException.class, () -> {
            service.execute(file, testUser);
        });

        verify(billRepository, never()).saveAll(anyList());
    }

    @Test
    public void shouldThrowExceptionForWrongColumnCount() {
        final String csvContent = "dueDate,paymentDate,amount,description,status\n" +
                "15/01/2025, 10/05/2025,100.50,Electricity bill";

        final MultipartFile file = createMockMultipartFile("wrong_columns.csv", csvContent);

        assertThrows(InvalidCSVFormatException.class, () -> {
            service.execute(file, testUser);
        });

        verify(billRepository, never()).saveAll(anyList());
    }

    @Test
    public void shouldHandleCommaDecimalSeparator() throws Exception {
        final String csvContent = "dueDate;paymentDate;amount;description;status\n" +
                "15/01/2025;10/01/2025;1234,56;Rent;PAID";

        final MultipartFile file = createMockMultipartFile("comma_decimal.csv", csvContent);

        when(billRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        service.execute(file, testUser);

        verify(billRepository).saveAll(billsCaptor.capture());
        final List<Bill> capturedBills = billsCaptor.getValue();

        assertEquals(1, capturedBills.size());
        assertEquals(1234.56, capturedBills.get(0).getAmount());
    }

    @Test
    public void shouldThrowExceptionWhenNoValidBills() {
        final String csvContent = "dueDate,paymentDate,amount,description,status";

        final MultipartFile file = createMockMultipartFile("no_data.csv", csvContent);

        assertThrows(InvalidCSVFormatException.class, () -> {
            service.execute(file, testUser);
        });

        verify(billRepository, never()).saveAll(anyList());
    }

    @Test
    public void shouldThrowExceptionForNullFile() {
        assertThrows(InvalidCSVFormatException.class, () -> {
            service.execute(null, testUser);
        });

        verify(billRepository, never()).saveAll(anyList());
    }

    private MockMultipartFile createMockMultipartFile(String filename, String content) {
        return new MockMultipartFile(
                "file",
                filename,
                "text/csv",
                content.getBytes(StandardCharsets.UTF_8)
        );
    }

}
