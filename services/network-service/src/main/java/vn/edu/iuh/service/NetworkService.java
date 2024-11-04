package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import vn.edu.iuh.client.WalletClient;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.exception.ErrorCode;
import vn.edu.iuh.mapper.NetworkMapper;
import vn.edu.iuh.model.Network;
import vn.edu.iuh.repository.NetworkRepository;
import vn.edu.iuh.request.CreateNetworkRequest;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NetworkService {
    private final NetworkRepository networkRepository;
    private final NetworkMapper networkMapper;
    private final WalletClient walletClient;
    @Value("${blockchain.infura-url}")
    private String infuraUrl;
    @Value("${blockchain.contract-address}")
    private String contractAddress;
    @Value("${blockchain.root-wallet.private-key}")
    private String rootPrivateKey;

    public List<Network> getNetworks(String token) {
        List<Long> networkIds = walletClient.getNetworkIdsByAccount(token).getResult();
        return networkRepository.findAllById(networkIds);
    }

    public Network createNetwork(CreateNetworkRequest request, String token) {
        Network network = networkMapper.toNetwork(request);
        network.setPassword(request.getName());
        try {
            String password = request.getName();
            File walletDirectory = new File("/wallet");
            if (!walletDirectory.exists()) {
                walletDirectory.mkdirs();
            }
            String walletFileName = WalletUtils.generateNewWalletFile(password, walletDirectory, true);
            Credentials credentials = WalletUtils.loadCredentials(password, new File(walletDirectory, walletFileName));
            System.out.println("Wallet Address: " + credentials.getAddress());
            System.out.println("Private Key: " + credentials.getEcKeyPair().getPrivateKey().toString(16));
            network.setWalletAddress(credentials.getAddress());
            network.setPrivateKey(credentials.getEcKeyPair().getPrivateKey().toString(16));
            network.setWalletPath(walletFileName);
            var balance = network.getMaxBalance() * network.getMaxMember();
            network.setBalance(balance);
            transferTokens(network.getWalletAddress(), balance, rootPrivateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        var savedNetwork = networkRepository.save(network);
        walletClient.createWallet(savedNetwork.getId(), token);
        return savedNetwork;
    }

    public void transferTokens(String recipientAddress, double tokenAmount, String privateKey) throws Exception {
        Web3j web3j = Web3j.build(new HttpService(infuraUrl));
        Credentials credentials = Credentials.create(privateKey);
        RawTransactionManager transactionManager = new RawTransactionManager(web3j, credentials);

        ContractGasProvider gasProvider = new DefaultGasProvider();

        BigInteger amountInWei = BigDecimal.valueOf(tokenAmount).multiply(BigDecimal.TEN.pow(18)).toBigInteger();

        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(recipientAddress), new Uint256(amountInWei)),
                Collections.emptyList()
        );

        String encodedFunction = FunctionEncoder.encode(function);

        EthSendTransaction transactionResponse = transactionManager.sendTransaction(
                gasProvider.getGasPrice(),
                gasProvider.getGasLimit(),
                contractAddress,
                encodedFunction,
                BigInteger.ZERO
        );

        if (transactionResponse.hasError()) {
            throw new RuntimeException("Transaction Error: " + transactionResponse.getError().getMessage());
        }
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
