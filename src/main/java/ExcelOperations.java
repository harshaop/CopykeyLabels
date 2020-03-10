import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class ExcelOperations {
    private final static Logger log = LoggerFactory.getLogger(CopyKeyValidation.class);

    public static void logToWorkbook(String key, String excel, String api, String fileName, String mkt) throws IOException {
        log.debug("Adding entry to excel file");
        Workbook workbook = new XSSFWorkbook(new FileInputStream(fileName));
        String sheetName = mkt.toUpperCase();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
            sheet.createFreezePane(0, 1);
            sheet.setDefaultColumnWidth(50);
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("Key");
            row.createCell(1).setCellValue("Label Description - Smartling");
            row.createCell(2).setCellValue("Label Description - FrontEnd-API");
            sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, 3));
        }

        int num = sheet.getLastRowNum();
        sheet.createRow(++num);
        sheet.getRow(num).createCell(0).setCellValue(key);
        sheet.getRow(num).createCell(1).setCellValue(excel);
        sheet.getRow(num).createCell(2).setCellValue(api);

        FileOutputStream fileOut = new FileOutputStream(fileName);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    public static void checkFileIfExists(String outFileName) throws IOException {
        String path = Paths.get("").toAbsolutePath().toString() + "\\";
        if (!new File(path + outFileName).isFile()) {
            log.info("Creating new Excel File for logging output " + outFileName);
            FileOutputStream fileOut = new FileOutputStream(path + outFileName);
            XSSFWorkbook workbook = new XSSFWorkbook();
            workbook.write(fileOut);
            workbook.close();
            fileOut.close();
        }
    }
}
