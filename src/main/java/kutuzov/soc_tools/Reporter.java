package kutuzov.soc_tools;

import kutuzov.soc_tools.entities.exelModel.ExelModel;
import kutuzov.soc_tools.entities.exelModel.MatrixExelCell;
import kutuzov.soc_tools.entities.fileSystemModel.AccessRule;
import kutuzov.soc_tools.entities.fileSystemModel.Directory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;


import java.util.*;

public class Reporter {
    final static int START_DIRECTORY_CELL_INDEX = 2;
    final static int START_USER_ROW_INDEX = 3;

    //------------------------Output to exel methods--------------------------------------------------------------------

    public static HSSFWorkbook writeExelReport(TreeMap<String, Directory> accessMatrix) {
        //Создание модели матрицы доступа для запси в exel-документ
        ExelModel exelMatrixForm = new ExelModel(accessMatrix);

        //Создание exel-документа
        HSSFWorkbook exelReport = new HSSFWorkbook();

        //Содание exel-листа в exel-документе
        Sheet sheet1 = exelReport.createSheet("directory1");


        fillDirectoryRowToExel(sheet1, exelMatrixForm);
        fillUserColumnToExel(sheet1, exelMatrixForm);
        fillAccessCellsToExel(sheet1, exelMatrixForm);


        return exelReport;
    }

    private static void fillDirectoryRowToExel(Sheet sheet, ExelModel exelMatrixForm) {
        //Создание строчки и запись в её ячейки
        Row directoryRow = sheet.createRow(2);

        ArrayList<String> directories = exelMatrixForm.getDirectoryList();
        //Запись строки ресурсов
        int directoryCellIndex = START_DIRECTORY_CELL_INDEX;
        for (String directory : directories) {
            Cell directoryCell = directoryRow.createCell(directoryCellIndex);
            directoryCell.setCellValue(directory);
            directoryCellIndex++;
        }
    }

    private static void fillUserColumnToExel(Sheet sheet, ExelModel exelMatrixForm) {
        int userCellIndex = START_USER_ROW_INDEX;


        ArrayList<String> users = exelMatrixForm.getUserList();

        //Заполнение колонки пользователей
        for (String user : users) {
            Row row = sheet.createRow(userCellIndex);

            Cell userCell = row.createCell(0);
            userCell.setCellValue(user);

            Cell userNameCell = row.createCell(1);
            userNameCell.setCellValue(exelMatrixForm.getUserName(user));


            userCellIndex++;
        }
    }

    private static void fillAccessCellsToExel(Sheet sheet, ExelModel exelMatrixForm) {
        ArrayList<MatrixExelCell> accessCells = exelMatrixForm.getAccessCells();

        for (MatrixExelCell cell : accessCells) {
            int rowIndex = cell.getRowIndex() + START_USER_ROW_INDEX;
            int cellIndex = cell.getColumnIndex() + START_DIRECTORY_CELL_INDEX;
            Row row = sheet.getRow(rowIndex);
            Cell accessCell = row.getCell(cellIndex);

            if (accessCell == null) {
                accessCell = row.createCell(cellIndex);
            }

            String accessValue = cell.getAccess();

            if (!accessValue.contentEquals("NOT_ACCESS")) {
                accessCell.setCellValue(Character.toString(accessValue.charAt(0)));
            }

        }

    }

    //------------------------Output to Screen methods--------------------------------------------------------------------

    public static void printAccessList(TreeMap<String, Directory> directoryAccessLists) {
        Set<String> dirPathSet = directoryAccessLists.keySet();
        for (String dir : dirPathSet) {
            Directory directory = directoryAccessLists.get(dir);
            TreeMap<String, AccessRule> accessRuleMap = directory.getAccessMap();
            System.out.println(directory.getName());

            HashMap<String, String> userMap = directory.getUserMap();

            for (String user : accessRuleMap.keySet()) {
                String userName = userMap.get(user);
                String denyGenericAccess = accessRuleMap.get(user).getGenericAccessDenyRight();
                String allowGenericAccess = accessRuleMap.get(user).getGenericAccessAllowRight();


                System.out.println("\t " + user
                        + "\\" + userName
                        + " : " + getResultGenericAccess(denyGenericAccess, allowGenericAccess));

            }
            System.out.println("\n\n");
        }
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


}
