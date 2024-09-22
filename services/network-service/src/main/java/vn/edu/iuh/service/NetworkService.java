package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.iuh.client.WalletClient;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.exception.ErrorCode;
import vn.edu.iuh.mapper.NetworkMapper;
import vn.edu.iuh.model.Network;
import vn.edu.iuh.repository.NetworkRepository;
import vn.edu.iuh.request.CreateNetworkRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NetworkService {
    private final NetworkRepository networkRepository;
    private final NetworkMapper networkMapper;
    private final WalletClient walletClient;

    public List<Network> getNetworks(String token) {
        List<Long> networkIds = walletClient.getNetworkIdsByAccount(token).getResult();
        return networkRepository.findAllById(networkIds);
    }

    public Network createNetwork(CreateNetworkRequest request, String token) {
        Network network = networkMapper.toNetwork(request);
        var savedNetwork = networkRepository.save(network);
        walletClient.createWallet(savedNetwork.getId(), token);
        return savedNetwork;
    }

    public Network joinNetwork(Long networkId, String token) {
        var network = findById(networkId);
        walletClient.createWallet(networkId, token);
        return network;
    }

    public Network findById(Long networkId) {
        return networkRepository.findById(networkId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }
}
