package tenshy.bills.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface Filter {

    int PAGE_SIZE = 10;

    void setPage(int page);
    int getPage();

    @JsonIgnore
    @Schema(hidden = true)
    default Pageable getPageable() {
        return PageRequest.of(getPage(), PAGE_SIZE);
    }

}
