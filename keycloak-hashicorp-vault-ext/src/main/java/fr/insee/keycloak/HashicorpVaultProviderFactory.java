package fr.insee.keycloak;

import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.vault.VaultNotFoundException;
import org.keycloak.vault.VaultProvider;
import org.keycloak.vault.VaultProviderFactory;

import fr.insee.vault.VaultService;

public class HashicorpVaultProviderFactory implements VaultProviderFactory {
   private static final Logger logger = Logger.getLogger(HashicorpVaultProviderFactory.class);

   public static final String PROVIDER_ID = "hachicorp-vault";

   private String vaultToken;
   private String vaultUrl;
   private String vaultSecretEngineName;

   @Override
   public VaultProvider create(KeycloakSession session) {
	   System.out.println("==============================create method of vault provider factory");
	   System.out.println("==================vaultToken====>"+vaultToken);
	   System.out.println("===============vaultUrl======>"+vaultUrl);
	   System.out.println("===============vaultSecretEngineName===>"+vaultSecretEngineName);
	   
      VaultService service = new VaultService(session);
      if(!service.isVaultAvailable(vaultUrl, vaultToken)){
         logger.error("====================Vault unavailable : "+ vaultUrl);
         throw new VaultNotFoundException("Vault unavailable : "+ vaultUrl);
      }else{
         logger.info("===================Vault available : "+ vaultUrl);
      }
      return new HashicorpVaultProvider(vaultUrl, vaultToken, session.getContext().getRealm().getName(), vaultSecretEngineName, service);

   }

   private static String format(String url) {
	   System.out.println("===========================format method of vault provider factory"+url);
	   if (!(url.charAt(url.length() - 1) == '/')) {
		   return url.concat("/");
	   }
	   else {
		   return url;
	   }
   }
  

   @Override
   public void init(Scope config) {
	   System.out.println("========================init method of vault provider factory"+config);
	   System.out.println("======================init method of vault provider factory token"+config.get("token"));
	   System.out.println("===========================init method of vault provider factory url"+config.get("url"));
	   System.out.println("===========================init method of vault provider factory engine-name"+config.get("engine-name"));
      if(System.getenv("VAULT_TOKEN") != null){
        vaultToken = System.getenv("VAULT_TOKEN");
      }else{
        vaultToken = config.get("token");
      }
      vaultUrl = config.get("url")!=null ? format(config.get("url")) : null;
      vaultSecretEngineName = config.get("engine-name");
      logger.info("======================================Init Hashicorp: "+ vaultUrl);
   }

   @Override
   public void postInit(KeycloakSessionFactory factory) {
	   System.out.println("===================postINIT method of vault provider factory");
      // TODO Auto-generated method stub

   }

   @Override
   public void close() {
	   System.out.println("========================close method of vault provider factory");
      // TODO Auto-generated method stub

   }

   @Override
   public String getId() {
      return PROVIDER_ID;
   }
   

   
}
