package vn.edu.iuh.component;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class FirebaseKey {
    @Value("${firebase.type}")
    private String type;
    @Value("${firebase.project-id}")
    private String projectId;
    @Value("${firebase.private-key-id}")
    private String privateKeyId;
    @Value("${firebase.private-key}")
    private String privateKey;
    @Value("${firebase.client-ent}")
    private String clientEnt;
    @Value("${firebase.client-id}")
    private String clientId;
    @Value("${firebase.auth-uri}")
    private String authUri;
    @Value("${firebase.token-uri}")
    private String tokenUri;
    @Value("${firebase.auth-provider-x509-cert-url}")
    private String authProviderCertUrl;
    @Value("${firebase.client-x509-cert-url}")
    private String clientX509CertUrl;
    @Value("${firebase.universe-domain}")
    private String universeDomain;
    
    //convert to json
    public String toString() {
        return "{\n" +
                "  \"type\": \"" + type + "\",\n" +
                "  \"project_id\": \"" + projectId + "\",\n" +
                "  \"private_key_id\": \"" + privateKeyId + "\",\n" +
                "  \"private_key\": \"" + privateKey + "\",\n" +
                "  \"client_email\": \"" + clientEnt + "\",\n" +
                "  \"client_id\": \"" + clientId + "\",\n" +
                "  \"auth_uri\": \"" + authUri + "\",\n" +
                "  \"token_uri\": \"" + tokenUri + "\",\n" +
                "  \"auth_provider_x509_cert_url\": \"" + authProviderCertUrl + "\",\n" +
                "  \"client_x509_cert_url\": \"" + clientX509CertUrl + "\"\n" +
                "}";
    }
}
