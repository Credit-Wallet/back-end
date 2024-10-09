package vn.edu.iuh.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.iuh.model.Currency;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateNetworkRequest {
    @Size(min = 6 , message = "Name must be at least 6 characters")
    private String name;
    private double minBalance;
    private double maxBalance;
    private Long maxMember;
    private Currency currency;
    private String description;
}
