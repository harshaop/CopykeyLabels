import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class ExcelOperations {
    private final static Logger log = LoggerFactory.getLogger(CopyKeyValidation.class);
    private static final String INPUT_FILE_NAME = "R20A_HM_Label File.xlsx";
    private static final String OUTPUT_FILE_NAME = "Output-Sheet.xlsx";
    private static final String OUTPUT_SHEET_NAME = "Output-Sheet";

    @NotNull
    public static LinkedHashMap<String, String> readExcel() throws IOException {

        Workbook workbook = WorkbookFactory.create(new File(INPUT_FILE_NAME));
        Sheet sheet = workbook.getSheetAt(1);
        DataFormatter dataFormatter = new DataFormatter();
        LinkedHashMap<String, String> hmap = new LinkedHashMap<>();

        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();
        log.info("Reading Data from the sheet: " + workbook.getSheetName(1) + " of the excel file: " + INPUT_FILE_NAME);
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (!isHidden(row)) {
                Cell keys = row.getCell(1);
                Cell label = row.getCell(14);
                String keyString = dataFormatter.formatCellValue(keys);
                String[] lines = keyString.split("[\\r\\n|\\t]+");
                String labelString = dataFormatter.formatCellValue(label);
                if (lines.length > 1)
                    for (String line : lines) hmap.put(line, labelString);
                else hmap.put(keyString, labelString);
            }
        }
        workbook.close();
        return hmap;
    }

    public static void modifyWorkbook(String key, String excel, String api) throws InvalidFormatException, IOException {
        log.debug("Adding entry to excel file");

        Workbook workbook = new XSSFWorkbook(new FileInputStream(OUTPUT_FILE_NAME));

        Sheet sheet = workbook.getSheet(OUTPUT_SHEET_NAME);
        int num = sheet.getLastRowNum();
        sheet.createRow(++num);
        sheet.getRow(num).createCell(0).setCellValue(key);
        sheet.getRow(num).createCell(1).setCellValue(excel);
        sheet.getRow(num).createCell(2).setCellValue(api);

        FileOutputStream fileOut = new FileOutputStream(OUTPUT_FILE_NAME);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    public static void checkFileStatus() throws IOException {
        String path = getPath();
        File file = new File(path + OUTPUT_FILE_NAME);
        XSSFWorkbook workbook;
        if (!file.exists()) {
            FileOutputStream fileOut = new FileOutputStream(path + OUTPUT_FILE_NAME);
            workbook = new XSSFWorkbook();
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        }
        FileOutputStream fileOut = new FileOutputStream(path + OUTPUT_FILE_NAME);
        workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(OUTPUT_SHEET_NAME);
        sheet.createFreezePane(0, 1);
        sheet.setDefaultColumnWidth(50);
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Key");
        row.createCell(1).setCellValue("Label Description - Excel");
        row.createCell(2).setCellValue("Label Description - API");
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, 3));

        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    private static boolean isHidden(@NotNull Row row) {
        return row.getZeroHeight();
    }

    @NotNull
    private static String getPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString() + "\\";
    }
}
