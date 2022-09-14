package kutuzov.soc_tools;

import kutuzov.soc_tools.entities.fileSystemModel.AccessRule;
import kutuzov.soc_tools.entities.fileSystemModel.Directory;
import kutuzov.soc_tools.entities.fileSystemModel.User;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclFileAttributeView;
import java.util.*;

public class AccessListExtractor {


    public static TreeMap<String, Directory> extractAccessMatrix(String rootCatalogPath, TreeMap<String, String> LDADUsers) {
        Path rootDirectory = Path.of(rootCatalogPath);

        TreeMap<String, Directory> accessMatrix = new TreeMap<>();

        try (DirectoryStream<Path> directoryPathList = Files.newDirectoryStream(rootDirectory)) {

            for (Path directoryPath : directoryPathList) {

                Directory directory = createDirectory(directoryPath, LDADUsers);
                accessMatrix.put(directoryPath.toString(), directory);
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        return accessMatrix;
    }

    private static Directory createDirectory(Path path, TreeMap<String, String> LDAPUsers) throws IOException {
        Directory directory = new Directory(path);

        AclFileAttributeView view = Files.getFileAttributeView(path, AclFileAttributeView.class);
        // read ACL from Directory (loaded from path)
        List<AclEntry> aclEntryList = view.getAcl();

        for (AclEntry acl : aclEntryList) {

            String samAccountName;
            if(acl.principal().getName().split("\\\\").length < 2){
                samAccountName = acl.principal().getName();
            }else{
                samAccountName = acl.principal().getName().split("\\\\")[1];
            }

            AccessRule accessRule = new AccessRule(acl);
            User user = new User(LDAPUsers.get(samAccountName), samAccountName);

            directory.addAccessRule(user, accessRule);
        }
        return directory;
    }


}

