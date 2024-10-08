package vn.edu.iuh.mapper;

import org.springframework.stereotype.Service;
import vn.edu.iuh.model.Bill;
import vn.edu.iuh.request.CreateBillRequest;

@Service
public class BillMapper {
    public Bill toBill(CreateBillRequest request){
        return Bill.builder()
                .name(request.getName())
                .amount(request.getAmount())
                .networkId(request.getNetworkId())
                .build();
    }
}
