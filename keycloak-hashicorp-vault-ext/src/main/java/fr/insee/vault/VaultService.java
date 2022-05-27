package fr.insee.vault;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jboss.logging.Logger;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;

/**
 * VaultService
 */
public class VaultService {

	private KeycloakSession session;
	private static final Logger logger = Logger.getLogger(VaultService.class);
	
	public VaultService(KeycloakSession session) {
		this.session = session;
	}
	
	public ByteBuffer getSecretFromVault(String vaultUrl, String realm, String vaultSecretEngineName, String secretName, String vaultToken, int secretVersion) {
		System.out.println("***********************************************************************************");
		System.out.println("vaultUrl====>"+vaultUrl);
		System.out.println("realm====>"+realm);
		System.out.println("vaultSecretEngineName====>"+vaultSecretEngineName);
		System.out.println("secretName====>"+secretName);
		System.out.println("vaultToken====>"+vaultToken);
		System.out.println("secretVersion====>"+secretVersion);
		System.out.println("-------------------------Printing URLS--------------"+vaultUrl + "v1/" + vaultSecretEngineName + "/data");
		System.out.println("***********************************************************************************");
		try {
			JsonNode node = SimpleHttp.doGet(vaultUrl + "v1/" + vaultSecretEngineName + "/data", session)
			.header("X-Vault-Token", vaultToken)
			.asJson();
			
			ObjectMapper mapper = new ObjectMapper(); 
			String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
			System.out.println("JSONNODE===============>"+pretty);
		   byte[] secretBytes = node.get("data").get(secretName).textValue().getBytes(StandardCharsets.UTF_8);
			return ByteBuffer.wrap(secretBytes);
		} catch (IOException e) {
			logger.error("secret not available", e);
			return null;
		}
	}

	public boolean isVaultAvailable(String vaultUrl, String vaultToken) {
		String healthVaultUrl = vaultUrl + "v1/sys/health";
		try {
			JsonNode vaultHealthResponseNode = SimpleHttp.doGet(healthVaultUrl, session)
					.asJson();
			boolean vaultIsInitialized = vaultHealthResponseNode.get("initialized").asBoolean();
			boolean vaultIsSealed = vaultHealthResponseNode.get("sealed").asBoolean();
			return (vaultIsInitialized && !vaultIsSealed);
		} catch (IOException e) {
			logger.error("====================================vault service unavailable", e);
			return false;
		}
	}

}