package tenshy.bills.shared.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor(staticName = "from")
public class PageableList<T> {

    private List<T> items;
    private int page;
    private int totalPages;
    private long totalItems;

}
