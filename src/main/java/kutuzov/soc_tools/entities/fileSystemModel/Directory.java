package kutuzov.soc_tools.entities.fileSystemModel;

import java.nio.file.Path;
import java.util.*;

public class Directory extends MatrixEntity {
    private final HashMap<String,String> userMap;
    private final TreeMap<String, AccessRule> accessMap;
    private final String path;
    private final String name;


    public Directory(Path dirPath) {
        this.accessMap = new TreeMap<>();
        this.userMap = new HashMap<>();
        this.path = dirPath.toString();
        this.name = dirPath.getFileName().toString();
    }


    public void addAccessRule(User user, AccessRule accessRule){
        AccessRule existingRule = this.accessMap.get(user.getSamAccountName());
        if(existingRule != null){
            existingRule.complementRule(accessRule);
            return;
        }

        this.userMap.put(user.getSamAccountName(),user.getUserName());
        this.accessMap.put(user.getSamAccountName(),accessRule);
    }

    public TreeMap<String, AccessRule> getAccessMap() {
        return accessMap;
    }

    public HashMap<String, String> getUserMap() {
        return userMap;
    }

    public Set<User> getUserList(){
        Set<User> users = new HashSet<>();
        Set<String> samAccountNames = this.userMap.keySet();

        for(String samAccountName:samAccountNames){
            String userName = this.getUserMap().get(samAccountName);
            users.add(new User(userName,samAccountName));
        }
        return users;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

}
