package kutuzov.soc_tools.entities.fileSystemModel;


import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class AccessRule extends MatrixEntity {
    private static final String[] readPermissions = {"READ_DATA", "READ_NAMED_ATTRS", "EXECUTE", "READ_ATTRIBUTES", "READ_ACL", "SYNCHRONIZE"};
    private static final String[] writePermissions = {"READ_DATA", "READ_NAMED_ATTRS", "EXECUTE", "READ_ATTRIBUTES", "READ_ACL", "SYNCHRONIZE", "WRITE_DATA", "WRITE_NAMED_ATTRS", "WRITE_ATTRIBUTES", "DELETE", "APPEND_DATA"};


    //From ACL of Directory.
    private final Set<String> allowPermissions;
    private final Set<String> denyPermissions;

    //READ, WRITE, FULL or OTHER permissions group
    private String genericAccessAllowRight;
    private String genericAccessDenyRight;

    public AccessRule(AclEntry aclEntry) {
       this.allowPermissions = new TreeSet<>();
       this.denyPermissions = new TreeSet<>();
       readPermissions(aclEntry);

       determinePermissionGroup();
    }



    private void readPermissions(AclEntry acl) {
        Set<String> permissions;
        String type = acl.type().name();
        if(type.contentEquals("ALLOW")){
            permissions = this.allowPermissions;
        }else if (type.contentEquals("DENY")){
            permissions = this.denyPermissions;
        }else return;

        Set<AclEntryPermission> aclEntryPermissions = acl.permissions();

        for (AclEntryPermission permission : aclEntryPermissions) {
            permissions.add(permission.name());
        }

    }

    private void determinePermissionGroup() {
        if (isFullGroup()) this.genericAccessAllowRight = "FULL";

        if (isWriteGroup()) this.genericAccessAllowRight = "WRITE";

        if (isReadGroup()) this.genericAccessAllowRight = "READ";

        if(allowPermissions.size() == 0) this.genericAccessAllowRight = "EMPTY";

        if (genericAccessAllowRight == null && allowPermissions.size() != 0) this.genericAccessAllowRight = "OTHER";

        if(isDenyReadGroup()) this.genericAccessDenyRight = "DENY_READ";

        if(isDenyWriteGroup()) this.genericAccessDenyRight = "DENY_WRITE";

        if(denyPermissions.size() == 0) this.genericAccessDenyRight = "DENY_EMPTY";

        if (genericAccessDenyRight == null && denyPermissions.size() != 0) this.genericAccessDenyRight = "DENY_OTHER";

    }

    private boolean isReadGroup() {
        int readPermCount = 0;

        //If the number of permissions isn't equals 6, then this set isn't a READ group.
        if (allowPermissions.size() != readPermissions.length) {
            return false;
        }

        //checking required for READ grope permissions in current permissions set.
        for (String permission : allowPermissions) {
            for (String readPermission : readPermissions) {
                if (permission.contentEquals(readPermission)) {
                    readPermCount++;
                    break;
                }
            }
        }


        //if all checking is success, then this set is a READ group
        return readPermCount == 6;
    }

    private boolean isWriteGroup() {
        int writePermCount = 0;


        //If the number of permissions isn't equals 11, then this set isn't a WRITE group.
        if (allowPermissions.size() != writePermissions.length) {
            return false;
        }

        //checking required for WRITE grope permissions in current permissions set.
        for (String permission : allowPermissions) {
            for (String writePermission : writePermissions) {
                if (permission.contentEquals(writePermission)) {
                    writePermCount++;
                    break;
                }
            }
        }


        //if all checking is success, then this set is a WRITE group
        return writePermCount == 11;
    }

    private boolean isFullGroup() {
        //If Permissions set has 14 elements, that means user has FULL access (we assume so)
        if(denyPermissions.size() == 14){
            return false;
        }
        return allowPermissions.size() == 14;

    }

    private boolean isDenyReadGroup(){
        int denyPermCount = 0;

        for (String permission : denyPermissions) {
            for (String readPermission : readPermissions) {
                if(readPermission.contentEquals("SYNCHRONIZE")){
                    continue;
                }

                if (permission.contentEquals(readPermission)) {
                    denyPermCount++;
                    break;
                }
            }
        }

        return denyPermCount == 5;
    }

    private boolean isDenyWriteGroup(){
        int denyWritePermCount = 0;

        for (String permission : denyPermissions) {
            for (String writePermission : writePermissions) {
                if(writePermission.contentEquals("SYNCHRONIZE")){
                    continue;
                }

                if (permission.contentEquals(writePermission)) {
                    denyWritePermCount++;
                    break;
                }
            }
        }

        return denyWritePermCount == 10;
    }





    public String getGenericAccessAllowRight() {
        return genericAccessAllowRight;
    }

    public String getGenericAccessDenyRight() {
        return genericAccessDenyRight;
    }


    private Set<String> getAllowPermissions() {
        return new HashSet<>(this.allowPermissions);
    }

    private Set<String> getDenyPermissions() {
        return new HashSet<>(this.denyPermissions);
    }

    protected void complementRule(AccessRule additionalAccessRule) {

        if(this.allowPermissions.size() != 0) {
            for (String additionalPermission : additionalAccessRule.getAllowPermissions()) {
                for (String currentPermission : this.getAllowPermissions()) {
                    if (!currentPermission.contentEquals(additionalPermission)) {
                        this.allowPermissions.add(additionalPermission);
                    }
                }
            }
        }else{
            this.allowPermissions.addAll(additionalAccessRule.allowPermissions);
        }

        if(this.denyPermissions.size() != 0) {
            for (String additionalPermission : additionalAccessRule.getDenyPermissions()) {
                for (String currentPermission : this.getDenyPermissions()) {
                    if (!currentPermission.contentEquals(additionalPermission)) {
                        this.denyPermissions.add(additionalPermission);
                    }
                }
            }
        }else{
            this.denyPermissions.addAll(additionalAccessRule.denyPermissions);
        }

        this.determinePermissionGroup();
    }


}
