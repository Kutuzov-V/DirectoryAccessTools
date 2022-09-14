package kutuzov.soc_tools;

import kutuzov.soc_tools.entities.config.Config;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;
import java.util.TreeMap;

public class LDAPExplorer {

    public static TreeMap<String, String> getUsers(Config config) throws NamingException {
        Hashtable<String, String> ldapEnv = config.getLdapEnv();
        String[] adCatalogs = config.getAdSearchCatalogs();


        TreeMap<String, String> userList = new TreeMap<>();
        for (String searchBase : adCatalogs) {
            InitialDirContext ldapContext;
            try {
                ldapContext = new InitialDirContext(ldapEnv);
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }

            SearchControls searchControls = new SearchControls();
            String[] returnedAttrs = {"samAccountName", "CN"};
            searchControls.setReturningAttributes(returnedAttrs);
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String searchFilter = "(&(objectCategory=person)(objectClass=user))";


            NamingEnumeration<SearchResult> results = null;


            try {
                // Производим поиск объектов (searchBase) на основе фильтра (SearchFilter)
                results = ldapContext.search(searchBase, searchFilter, searchControls);

                // Вызываем цикл для поиска объектов в полученном ответе от LDAP
                while (results.hasMore()) {
                    SearchResult searchResult = results.next();
                    Attributes attributes = searchResult.getAttributes();
                    String cn = attributes.get("CN").toString().split(": ")[1];
                    String samAccountName = attributes.get("samAccountName").toString().split(": ")[1];

                    try {
                        userList.put(samAccountName, cn);
                    } catch (NullPointerException npe) {
                        System.out.println("ошибка" + npe);
                        continue;
                    }
                }
            } catch (NameNotFoundException e) {
                // The base context was not found.
                // Just clean up and exit.
            } catch (NamingException e) {
                throw new RuntimeException(e);
            } finally {
                if (results != null) {
                    try {
                        results.close();
                    } catch (Exception e) {
                        // Never mind this.
                    }
                }
                if (ldapContext != null) {
                    try {
                        ldapContext.close();
                    } catch (Exception e) {
                        // Never mind this.
                    }
                }
            }
        }
        return userList;
    }

}
