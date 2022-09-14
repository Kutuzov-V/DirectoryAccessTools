package kutuzov.soc_tools.entities.config;

import javax.naming.Context;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Hashtable;

public class Config implements Serializable {

    private static final long serialVersionUID = 385193454;

    private final Hashtable<String, String> ldapEnv;
    private String baseCatalog;
    String[] adSearchCatalogs;


    public Config() {
        ldapEnv = new Hashtable<>(11);
        ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple"); //Тип аутентификации
        ldapEnv.put("com.sun.jndi.ldap.connect.pool", "true");
        ldapEnv.put("java.naming.ldap.attributes.binary", "objectSID");

    }

    public void setAdSearchCatalogs(String[] adSearchCatalogs) {
        this.adSearchCatalogs = adSearchCatalogs;
    }

    public void setProviderURL(String providerURL) {
        this.ldapEnv.put(Context.PROVIDER_URL, providerURL);
    }

    public void setLogin(String login) {
        //Логин пользователя от учётки которого мы делаем запрос в AD (LDAP) в формате <имя пользователя>@<имя домена>
        this.ldapEnv.put(Context.SECURITY_PRINCIPAL, login);
    }

    public void setPass(String pass) {
        this.ldapEnv.put(Context.SECURITY_CREDENTIALS, pass);
    }

    public void setBaseCatalog(String baseCatalog) {
        this.baseCatalog = baseCatalog;
    }

    public Hashtable<String, String> getLdapEnv() {
        return new Hashtable<>(ldapEnv);
    }

    public String getBaseCatalog() {
        return baseCatalog;
    }

    public String[] getAdSearchCatalogs() {
        return adSearchCatalogs;
    }
}
