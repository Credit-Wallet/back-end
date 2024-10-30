package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import vn.edu.iuh.client.AccountClient;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.exception.ErrorCode;
import vn.edu.iuh.model.Wallet;
import vn.edu.iuh.repository.WalletRepository;
import vn.edu.iuh.response.WalletResponse;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final AccountClient accountClient;

    public WalletResponse getWallet(String token) throws IOException {
        var account = accountClient.getProfile(token).getResult();
        var wallet = walletRepository.findByAccountIdAndNetworkId(account.getId(), account.getSelectedNetworkId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        Web3j web3j = Web3j.build(new HttpService("https://eth-sepolia.g.alchemy.com/v2/gIyKgeCxAHZLnSBjmSwTTxbK_ur45AfJ"));
        String walletAddress = wallet.getWalletAddress();
        String tokenContractAddress = "0xA88657562e04031E4b6Bb3fc80e2BC4E4c2436A9";

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

    public boolean transfer(Long fromId, Long toId,Long networkId, double amount) throws IOException {
        Wallet from = walletRepository.findByAccountIdAndNetworkId(fromId, networkId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        Wallet to = walletRepository.findByAccountIdAndNetworkId(toId, networkId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        String fromAddress = from.getWalletAddress();
        String toAddress = to.getWalletAddress();
//        Key để trả phí giao dịch
        String privateKey = "250e0b1c3d18b24f3cd8bfde392a4d1a6776a9f42f13fcb46134e4a135781dfc";

        Web3j web3j = Web3j.build(new HttpService("https://eth-sepolia.g.alchemy.com/v2/gIyKgeCxAHZLnSBjmSwTTxbK_ur45AfJ"));
        Credentials credentials = Credentials.create(privateKey);
        RawTransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
        String tokenContractAddress = "0xA88657562e04031E4b6Bb3fc80e2BC4E4c2436A9";
        ContractGasProvider gasProvider = new DefaultGasProvider();
        BigInteger amountInWei = BigDecimal.valueOf(amount).multiply(BigDecimal.TEN.pow(18)).toBigInteger();
        Function approveFunction = new Function(
                "approve",
                Arrays.asList(new Address(fromAddress), new Uint256(amountInWei)),
                Collections.emptyList()
        );
        String encodedApprove = FunctionEncoder.encode(approveFunction);
        transactionManager.sendTransaction(
                gasProvider.getGasPrice(),
                gasProvider.getGasLimit(),
                tokenContractAddress,
                encodedApprove,
                BigInteger.ZERO
        );

        Function transferFromFunction = new Function(
                "transferFrom",
                Arrays.asList(new Address(fromAddress), new Address(toAddress), new Uint256(amountInWei)),
                Collections.emptyList()
        );
        String encodedTransferFrom = FunctionEncoder.encode(transferFromFunction);
        transactionManager.sendTransaction(
                gasProvider.getGasPrice(),
                gasProvider.getGasLimit(),
                tokenContractAddress,
                encodedTransferFrom,
                BigInteger.ZERO
        );
        return true;
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

    public Wallet updateBalance(Long accountId, Long networkId, double amount) {
        var wallet = getWallet(accountId, networkId);
        wallet.setBalance(wallet.getBalance() + amount);
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
