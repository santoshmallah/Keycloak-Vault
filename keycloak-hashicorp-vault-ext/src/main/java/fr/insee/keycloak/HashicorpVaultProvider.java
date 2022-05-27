package fr.insee.keycloak;

import java.util.Optional;

import org.jboss.logging.Logger;
import org.keycloak.vault.DefaultVaultRawSecret;
import org.keycloak.vault.VaultProvider;
import org.keycloak.vault.VaultRawSecret;

import fr.insee.vault.VaultService;

/**
 * HashicorpVaultProviderFactory
 */
public class HashicorpVaultProvider implements VaultProvider {
	
   private static final Logger logger = Logger.getLogger(HashicorpVaultProviderFactory.class);

   private String vaultUrl;
   private String vaultToken;
   private String realmName;
   private String vaultSecretEngineName;
   private VaultService service;

   @Override
   public VaultRawSecret obtainSecret(String vaultSecretId) {
	   System.out.println("============================obtainSecret method of vault provider");
      int secretVersion = 0;
      String vaultSecretName = vaultSecretId;
      if (vaultSecretId.contains(":")) {
         try {
            secretVersion = Integer.parseInt(vaultSecretId.substring(vaultSecretId.lastIndexOf(":") + 1));
            vaultSecretName = vaultSecretId.substring(0, vaultSecretId.lastIndexOf(":"));
         } catch (NumberFormatException e) {
            logger.error("==========================last string after : is expected to be the version number");
         }
      }

     return DefaultVaultRawSecret.forBuffer(
         Optional.of(
            service.getSecretFromVault(vaultUrl, realmName, vaultSecretEngineName, vaultSecretName, vaultToken, secretVersion)));
   }

   @Override
   public void close() {
	   System.out.println("==========================close method of vault provider");
   }

   public HashicorpVaultProvider(String vaultUrl, String vaultToken, String realmName, String vaultSecretEngineName, VaultService service) {
	   System.out.println("======================HashicorpVaultProvider method of vault provider");
      this.vaultUrl = vaultUrl;
      this.vaultToken = vaultToken;
      this.realmName = realmName;
      this.vaultSecretEngineName = vaultSecretEngineName;
      this.service = service;
   }


   
   
}