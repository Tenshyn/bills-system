package tenshy.bills.bill.controllers.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tenshy.bills.bill.application.dtos.BillDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor(staticName = "from")
@Schema(description = "Bill paginated response")
public class BillPageableResponse {

    private List<BillDTO> bills;
    private int page;
    private int totalPages;
    private long totalItems;

}
