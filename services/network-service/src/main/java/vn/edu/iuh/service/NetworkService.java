package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.iuh.client.AccountClient;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.exception.ErrorCode;
import vn.edu.iuh.mapper.NetworkMapper;
import vn.edu.iuh.model.AccountNetwork;
import vn.edu.iuh.model.Network;
import vn.edu.iuh.repository.AccountNetworkRepository;
import vn.edu.iuh.repository.NetworkRepository;
import vn.edu.iuh.request.CreateNetworkRequest;
import vn.edu.iuh.response.AccountResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class NetworkService {
    private final NetworkRepository networkRepository;
    private final AccountNetworkRepository accountNetworkRepository;
    private final NetworkMapper networkMapper;
    private final AccountClient accountClient;

    public Network createNetwork(CreateNetworkRequest request, String token) {
        AccountResponse account = accountClient.getProfile(token).getResult();
        Network network = networkMapper.toNetwork(request);
        var savedNetwork = networkRepository.save(network);
        accountNetworkRepository.save(
                AccountNetwork.builder()
                        .accountId(account.getId())
                        .networkId(savedNetwork.getId())
                        .build());
        return savedNetwork;
    }

    public Network joinNetwork(Long networkId, String token) {
        AccountResponse account = accountClient.getProfile(token).getResult();
        var network = findById(networkId);
        if (accountNetworkRepository.existsByAccountIdAndNetworkId(account.getId(), networkId)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        accountNetworkRepository.save(
                AccountNetwork.builder()
                        .accountId(account.getId())
                        .networkId(network.getId())
                        .build());
        return network;
    }

    public Network findById(Long networkId) {
        return networkRepository.findById(networkId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }
}
