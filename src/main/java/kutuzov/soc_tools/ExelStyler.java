package kutuzov.soc_tools;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class ExelStyler {
    private static HSSFWorkbook exelReport;
    private static String sheetName;

    public static void applyStyle(HSSFWorkbook exelReport, String sheetName) {
        ExelStyler.exelReport = exelReport;
        ExelStyler.sheetName = sheetName;

        applyDirectoryCellStyle();
        applyUserCellStyle();
        applyUserNameCellStyle();
        applyAccessCellStyle();
    }

    private static void applyDirectoryCellStyle() {

        HSSFCellStyle directoryCellStyle = exelReport.createCellStyle();

        directoryCellStyle.setRotation((short) 90);
        directoryCellStyle.setBorderBottom(BorderStyle.THIN);
        directoryCellStyle.setBorderTop(BorderStyle.THIN);
        directoryCellStyle.setBorderLeft(BorderStyle.THIN);
        directoryCellStyle.setBorderRight(BorderStyle.THIN);


        directoryCellStyle.setAlignment(HorizontalAlignment.CENTER);
        directoryCellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
        directoryCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font directoryCellFont = exelReport.createFont();
        directoryCellFont.setFontHeightInPoints((short) 16);
        directoryCellFont.setFontName("Calibri");
        directoryCellFont.setColor(IndexedColors.WHITE.getIndex());

        directoryCellStyle.setFont(directoryCellFont);


        Sheet sheet = exelReport.getSheet(sheetName);
        int rowNumber = Reporter.START_DIRECTORY_CELL_INDEX;
        int startIndex = Reporter.START_DIRECTORY_CELL_INDEX;
        Row directoryRow = sheet.getRow(rowNumber);

        for (int i = startIndex; i <= directoryRow.getPhysicalNumberOfCells() + 1; i++) {
            directoryRow.getCell(i).setCellStyle(directoryCellStyle);
            sheet.setColumnWidth(i, 1500);
        }

        sheet.autoSizeColumn(1);


    }

    private static void applyUserCellStyle() {

        HSSFCellStyle userCellStyle = exelReport.createCellStyle();


        userCellStyle.setBorderBottom(BorderStyle.DOTTED);
        userCellStyle.setBorderTop(BorderStyle.DOTTED);
        userCellStyle.setBorderLeft(BorderStyle.DOTTED);


        Font directoryCellFont = exelReport.createFont();
        directoryCellFont.setFontHeightInPoints((short) 12);
        directoryCellFont.setFontName("Calibri");


        userCellStyle.setFont(directoryCellFont);


        Sheet sheet = exelReport.getSheet(sheetName);
        int startRowIndex = Reporter.START_USER_ROW_INDEX;


        for (int i = startRowIndex; i <= sheet.getPhysicalNumberOfRows() + 1; i++) {
            Row userRow = sheet.getRow(i);
            Cell userCell = userRow.getCell(0);

            userCell.setCellStyle(userCellStyle);

        }
        sheet.setColumnWidth(0, 4000);

    }

    private static void applyUserNameCellStyle() {

        HSSFCellStyle userNameCellStyle = exelReport.createCellStyle();

        userNameCellStyle.setBorderBottom(BorderStyle.DOTTED);
        userNameCellStyle.setBorderTop(BorderStyle.DOTTED);
        userNameCellStyle.setBorderRight(BorderStyle.THIN);


        Font directoryCellFont = exelReport.createFont();
        directoryCellFont.setFontHeightInPoints((short) 12);
        directoryCellFont.setFontName("Calibri");


        userNameCellStyle.setFont(directoryCellFont);


        Sheet sheet = exelReport.getSheet(sheetName);
        int startRowIndex = Reporter.START_USER_ROW_INDEX;

        for (int i = startRowIndex; i <= sheet.getPhysicalNumberOfRows() + 1; i++) {
            Row userNameRow = sheet.getRow(i);
            userNameRow.getCell(1).setCellStyle(userNameCellStyle);

        }

        sheet.autoSizeColumn(1);
        sheet.createFreezePane(0, 3);
    }

    private static void applyAccessCellStyle() {

        HSSFCellStyle accessCellStyle = exelReport.createCellStyle();
        HSSFCellStyle accessReadCellStyle = exelReport.createCellStyle();
        HSSFCellStyle accessWriteCellStyle = exelReport.createCellStyle();
        HSSFCellStyle accessFullCellStyle = exelReport.createCellStyle();
        HSSFCellStyle accessOtherCellStyle = exelReport.createCellStyle();

        accessCellStyle.setBorderBottom(BorderStyle.THIN);
        accessCellStyle.setBorderTop(BorderStyle.THIN);
        accessCellStyle.setBorderRight(BorderStyle.THIN);
        accessCellStyle.setBorderLeft(BorderStyle.THIN);

        accessCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        accessCellStyle.setFillForegroundColor(IndexedColors.WHITE.index);

        accessCellStyle.setAlignment(HorizontalAlignment.CENTER);
        accessCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        accessWriteCellStyle.cloneStyleFrom(accessCellStyle);
        accessWriteCellStyle.setFillForegroundColor(IndexedColors.GREEN.index);


        accessReadCellStyle.cloneStyleFrom(accessCellStyle);
        accessReadCellStyle.setFillForegroundColor(IndexedColors.YELLOW.index);


        accessFullCellStyle.cloneStyleFrom(accessCellStyle);
        accessFullCellStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());


        accessOtherCellStyle.cloneStyleFrom(accessCellStyle);
        accessOtherCellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);


        Font directoryCellFont = exelReport.createFont();
        directoryCellFont.setFontHeightInPoints((short) 12);
        directoryCellFont.setFontName("Calibri");


        accessCellStyle.setFont(directoryCellFont);


        Sheet sheet = exelReport.getSheet(sheetName);
        int startRowIndex = Reporter.START_USER_ROW_INDEX;
        int startCellIndex = Reporter.START_DIRECTORY_CELL_INDEX;
        int endCellIndex = sheet.getRow(startRowIndex - 1).getPhysicalNumberOfCells();

        for (int i = startRowIndex; i <= sheet.getPhysicalNumberOfRows() + 1; i++) {
            Row accessRow = sheet.getRow(i);
            for (int j = startCellIndex; j <= endCellIndex + 1; j++) {
                Cell cell = accessRow.getCell(j);
                if (cell == null) {
                    cell = accessRow.createCell(j);
                }

                String accessCellValue = cell.getStringCellValue();

                switch (accessCellValue) {
                    case "R":
                        cell.setCellStyle(accessReadCellStyle);
                        break;
                    case "W":
                        cell.setCellStyle(accessWriteCellStyle);
                        break;
                    case "F":
                        cell.setCellStyle(accessFullCellStyle);
                        break;
                    case "O":
                        cell.setCellStyle(accessOtherCellStyle);
                        break;
                    default:
                        cell.setCellStyle(accessCellStyle);
                }

            }
        }

    }

}
