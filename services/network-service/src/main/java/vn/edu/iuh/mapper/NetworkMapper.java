package vn.edu.iuh.mapper;

import org.springframework.stereotype.Service;
import vn.edu.iuh.model.Network;
import vn.edu.iuh.request.CreateNetworkRequest;

@Service
public class NetworkMapper {
    public Network toNetwork(CreateNetworkRequest request) {
        return Network.builder()
                .name(request.getName())
                .minBalance(request.getMinBalance())
                .maxBalance(request.getMaxBalance())
                .maxMember(request.getMaxMember())
                .currency(request.getCurrency())
                .build();
    }
}
