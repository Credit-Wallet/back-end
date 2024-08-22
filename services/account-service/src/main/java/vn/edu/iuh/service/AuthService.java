package vn.edu.iuh.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.iuh.exception.AppException;
import vn.edu.iuh.exception.ErrorCode;
import vn.edu.iuh.mapper.AccountMapper;
import vn.edu.iuh.model.Account;
import vn.edu.iuh.repository.AccountRepository;
import vn.edu.iuh.request.LoginRequest;
import vn.edu.iuh.request.RegisterRequest;
import vn.edu.iuh.response.AccountResponse;
import vn.edu.iuh.response.LoginResponse;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    public AccountResponse register(RegisterRequest request) throws AppException {
        if (accountRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.ACCOUNT_EXISTED);

        if(!request.getPassword().equals(request.getConfirmPassword()))
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);

        Account account = accountMapper.toAccount(request);
        account.setPassword(passwordEncoder.encode(request.getPassword()));

        return accountMapper.toAccountResponse(accountRepository.save(account));
    }

    public LoginResponse login(LoginRequest request) throws AppException {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        Account account = findByEmail(request.getEmail());

        boolean authenticated = passwordEncoder.matches(request.getPassword(), account.getPassword());

        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(account);

        return LoginResponse.builder().token(token).build();
    }

    private String generateToken(Account account) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(account.getEmail())
                .issuer("vn.edu.iuh")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    public String extractEmail(String token) {
        try {
            verifyToken(token);
            JWSObject jwsObject = JWSObject.parse(token);
            return jwsObject.getPayload().toJSONObject().get("sub").toString();
        } catch (ParseException | JOSEException e) {
            log.error("Cannot extract email from token", e);
            throw new RuntimeException(e);
        }
    }

    public AccountResponse checkMe() throws AppException {
        return accountMapper.toAccountResponse(getAccountLogin());
    }

    public Account getAccountLogin() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        return findByEmail(email);
    }

    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    public AccountResponse getProfile(String token) {
        token = token.substring(7);
        String email = extractEmail(token);
        return accountMapper.toAccountResponse(findByEmail(email));
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }
}
