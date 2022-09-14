package kutuzov.soc_tools.entities.fileSystemModel;

public class User {
    final private String samAccountName;
    final private String userName;

    public User(String userName, String samAccountName) {
        this.samAccountName = samAccountName;
        this.userName = userName;
    }

    public String getSamAccountName() {
        return samAccountName;
    }

    public String getUserName() {
        return userName;
    }
}
