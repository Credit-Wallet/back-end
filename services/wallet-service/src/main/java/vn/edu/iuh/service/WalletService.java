package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import vn.edu.iuh.client.AccountClient;
import vn.edu.iuh.client.NetworkClient;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.exception.ErrorCode;
import vn.edu.iuh.model.Wallet;
import vn.edu.iuh.repository.WalletRepository;
import vn.edu.iuh.response.WalletResponse;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final AccountClient accountClient;
    private final NetworkClient networkClient;
    private final RestTemplate restTemplate;
    @Value("${blockchain.transfer.url}")
    private String transferUrl;
    @Value("${blockchain.infura-url}")
    private String infuraUrl;
    @Value("${blockchain.contract-address}")
    private String contractAddress;
    @Value("${blockchain.root-wallet.address}")
    private String rootWalletAddress;
    @Value("${blockchain.root-wallet.private-key}")
    private String rootWalletPrivateKey;

    public WalletResponse getWallet(String token) throws IOException {
        var account = accountClient.getProfile(token).getResult();
        var wallet = walletRepository.findByAccountIdAndNetworkId(account.getId(), account.getSelectedNetworkId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        Web3j web3j = Web3j.build(new HttpService(infuraUrl));
        String walletAddress = wallet.getWalletAddress();
        String tokenContractAddress = contractAddress;

        Function function = new Function(
                "balanceOf",
                Collections.singletonList(new Address(walletAddress)),
                Collections.singletonList(new TypeReference<Uint256>() {
                })
        );

        String encodedFunction = FunctionEncoder.encode(function);
        EthCall response = web3j.ethCall(
                org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(walletAddress, tokenContractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST
        ).send();
        List<Type> results = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        BigInteger balance = (BigInteger) results.get(0).getValue();
        var balanceOf = Convert.fromWei(balance.toString(), Convert.Unit.ETHER);

        System.out.println("Token Balance: " + balanceOf);
        return WalletResponse.builder()
                .id(wallet.getId())
                .accountId(wallet.getAccountId())
                .balance(wallet.getBalance())
                .networkId(wallet.getNetworkId())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .walletAddress(wallet.getWalletAddress())
                .balanceOf(balanceOf.doubleValue())
                .build();
    }

    public String  transfer(Long fromId, Long toId, Long networkId, double amount) throws Exception {
        var network = networkClient.getNetworkById(networkId).getResult();
        System.out.println("Network: " + network.getPrivateKey());
        System.out.println("Transfering " + amount + " tokens from " + fromId + " to " + toId);
        String fromAddress = network.getWalletAddress();
        String toAddress = rootWalletAddress;
        String privateKey = network.getPrivateKey();
        if(fromId != 0L){
            Wallet from = walletRepository.findByAccountIdAndNetworkId(fromId, networkId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

            fromAddress = from.getWalletAddress();
            privateKey = from.getPrivateKey();
        }
        if(toId != 0L){
            Wallet to = walletRepository.findByAccountIdAndNetworkId(toId, networkId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
            toAddress = to.getWalletAddress();
        }

        String url = transferUrl;
        String body = "{\n" +
                "    \"privateKey\": \"" + privateKey + "\",\n" +
                "    \"sender\": \"" + fromAddress + "\",\n" +
                "    \"receiver\": \"" + toAddress + "\",\n" +
                "    \"amount\": " + amount + "\n" +
                "}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        return response.getBody();
    }

    public Wallet createWallet(Long networkId, String token) {
        var account = accountClient.getProfile(token).getResult();
        try {
            String password = account.getEmail();
            File walletDirectory = new File("/wallet");
            if (!walletDirectory.exists()) {
                walletDirectory.mkdirs();
            }
            String walletFileName = WalletUtils.generateNewWalletFile(password, walletDirectory, true);
            System.out.println("Wallet created: " + walletFileName);

            Credentials credentials = WalletUtils.loadCredentials(password, new File(walletDirectory, walletFileName));
            System.out.println("Wallet Address: " + credentials.getAddress());
            System.out.println("Private Key: " + credentials.getEcKeyPair().getPrivateKey().toString(16));

            var wallet = Wallet.builder()
                    .accountId(account.getId())
                    .networkId(networkId)
                    .password(password)
                    .walletAddress(credentials.getAddress())
                    .privateKey(credentials.getEcKeyPair().getPrivateKey().toString(16))
                    .walletPath(walletFileName)
                    .debt(0)
                    .balance(0)
                    .build();
            if (walletRepository.existsByAccountIdAndNetworkId(wallet.getAccountId(), wallet.getNetworkId())) {
                throw new AppException(ErrorCode.BAD_REQUEST);
            }
            return walletRepository.save(wallet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new AppException(ErrorCode.BAD_REQUEST);
    }

    public Wallet getWallet(Long accountId, Long networkId) {
        return walletRepository.findByAccountIdAndNetworkId(accountId, networkId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    public Wallet sendBalance(Long accountId, Long networkId, double amount) {
        var wallet = getWallet(accountId, networkId);
        System.out.println("Amount sender: " + amount);
        System.out.println("Balance of sender: " + wallet.getBalance());
        System.out.println("Debt of sender: " + wallet.getDebt());
        if(wallet.getBalance() < amount){
            if(wallet.getBalance() > 0){
                double temp = amount - wallet.getBalance();
                wallet.setDebt(wallet.getDebt() + temp);
                wallet.setBalance(-temp);
            }else{
                wallet.setDebt(wallet.getDebt() + amount);
                wallet.setBalance(wallet.getBalance() - amount);
            }
        }else{
            wallet.setBalance(wallet.getBalance() - amount);
        }
        return walletRepository.save(wallet);
    }

    public Wallet receiveBalance(Long accountId, Long networkId, double amount) {
        var wallet = getWallet(accountId, networkId);
        System.out.println("Amount receiver: " + amount);
        System.out.println("Balance of receiver: " + wallet.getBalance());
        System.out.println("Debt of receiver: " + wallet.getDebt());
        if(wallet.getDebt() > 0){
            if(wallet.getDebt() > amount){
                wallet.setDebt(wallet.getDebt() - amount);
                wallet.setBalance(wallet.getBalance() + amount);
            }else{
                wallet.setDebt(0);
                wallet.setBalance(wallet.getBalance() + amount);
            }
        }else{
            wallet.setBalance(wallet.getBalance() + amount);
        }
        return walletRepository.save(wallet);
    }

    public List<Long> getNetworkIdsByAccount(String token) {
        var account = accountClient.getProfile(token).getResult();
        return walletRepository.findByAccountId(account.getId())
                .stream()
                .map(Wallet::getNetworkId)
                .collect(Collectors.toList());
    }

    public List<Long> getAccountIdsByNetwork(Long networkId, String token) {
        var account = accountClient.getProfile(token).getResult();
        return walletRepository.findByNetworkId(networkId)
                .stream().map(Wallet::getAccountId)
                .filter(accountId -> !accountId.equals(account.getId()))
                .collect(Collectors.toList());
    }
}
