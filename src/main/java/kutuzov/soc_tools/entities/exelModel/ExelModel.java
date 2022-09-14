package kutuzov.soc_tools.entities.exelModel;

import kutuzov.soc_tools.entities.fileSystemModel.AccessRule;
import kutuzov.soc_tools.entities.fileSystemModel.Directory;

import java.util.*;

public class ExelModel {
    private ArrayList<MatrixExelCell> accessCells;
    private TreeMap<String,String> userMap;
    private ArrayList<String> userList;
    private ArrayList<String> directoryList;

    //-----------------------------------Constructor--------------------------------------------------------------------

    public ExelModel(TreeMap<String, Directory> accessMatrix) {
        this.fillUsersListFromMatrix(accessMatrix);
        this.fillDirectoryListFromMatrix(accessMatrix);
        this.createAccessCellsFromMatrix(accessMatrix);
    }


    private  void createAccessCellsFromMatrix(TreeMap<String, Directory> accessMatrix) {
        ArrayList<MatrixExelCell> accessCells = new ArrayList<>();
        TreeMap<String,String> userMap = new TreeMap<>();

        Set<String> directoryPaths = accessMatrix.keySet();
        for (String directoryPath : directoryPaths) {
            Directory directory = accessMatrix.get(directoryPath);
            HashMap<String, String> directoryUserMap = directory.getUserMap();
            TreeMap<String, AccessRule> accessMap = directory.getAccessMap();

            Set<String> users = accessMap.keySet();
            for (String user : users) {
                int userRowIndex;
                int directoryCellIndex;
                String directoryName = directory.getName();
                String userName = directoryUserMap.get(user);
                AccessRule access = accessMap.get(user);
                String accessStr = getResultGenericAccess(access.getGenericAccessDenyRight(), access.getGenericAccessAllowRight());

                userMap.put(user,userName);
                userRowIndex = userList.indexOf(user);
                directoryCellIndex = directoryList.indexOf(directoryName);

                accessCells.add(new MatrixExelCell(userRowIndex, directoryCellIndex, accessStr, user));

            }
        }
        this.accessCells = accessCells;
        this.userMap = userMap;
    }

    private static String getResultGenericAccess(String denyGenericAccess, String allowGenericAccess) {
        String result = new String();
        switch (denyGenericAccess) {
            case "DENY_EMPTY":
                result = allowGenericAccess;
                break;
            case "DENY_READ":
                result = "NOT_ACCESS";
                break;
            case "DENY_WRITE":
                result = "NOT_ACCESS";
                break;
            case "DENY_OTHER":
                result = "OTHER";
                break;
        }

        if (result.contentEquals("EMPTY")) {
            return "NOT_ACCESS";
        } else {
            return result;
        }

    }

    private void fillUsersListFromMatrix(TreeMap<String, Directory> accessMatrix) {
        Set<String> directoryPaths = accessMatrix.keySet();
        Set<String> users = new TreeSet<>();

        //Get sorted user list
        for (String directoryPath : directoryPaths) {
            Directory directory = accessMatrix.get(directoryPath);
            users.addAll(directory.getAccessMap().keySet());
        }

        this.userList = new ArrayList<>(users);

    }

    private void fillDirectoryListFromMatrix(TreeMap<String, Directory> accessMatrix) {
        Set<String> directoryPaths = accessMatrix.keySet();
        Set<String> directoryNames = new TreeSet<>();
        for (String directoryPath : directoryPaths) {
            Directory directory = accessMatrix.get(directoryPath);
            directoryNames.add(directory.getName());
        }
        this.directoryList =  new ArrayList<>(directoryNames);
    }


    //----------------------------------Get methods---------------------------------------------------------------------

    public ArrayList<String> getDirectoryList() {
        return new ArrayList<>(directoryList);
    }

    public ArrayList<MatrixExelCell> getAccessCells() {
        return new ArrayList<>(this.accessCells);
    }

    public String getUserName(String samAccountName){
        return userMap.get(samAccountName);
    }

    public ArrayList<String> getUserList() {
        return new ArrayList<>(userList);
    }
}
