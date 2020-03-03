import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

public class ExcelOperations {
    private final static Logger log = LoggerFactory.getLogger(CopyKeyValidation.class);
    private static final String OUTPUT_FILE_NAME = "Output-Sheet.xlsx";


    public static void modifyWorkbook(String key, String excel, String api, String env, String mkt) throws InvalidFormatException, IOException {
        log.debug("Adding entry to excel file");

        Workbook workbook = new XSSFWorkbook(new FileInputStream(OUTPUT_FILE_NAME));

        final String sheetName = env.toUpperCase() + " " + mkt.toUpperCase();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
            sheet.createFreezePane(0, 1);
            sheet.setDefaultColumnWidth(50);
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("Key");
            row.createCell(1).setCellValue("Label Description - Smartling");
            row.createCell(2).setCellValue("Label Description - API");
            sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, 3));
        }

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

        if (!new File(path + OUTPUT_FILE_NAME).isFile()) {
            FileOutputStream fileOut = new FileOutputStream(path + OUTPUT_FILE_NAME);
            XSSFWorkbook workbook = new XSSFWorkbook();
            workbook.write(fileOut);
            workbook.close();
            fileOut.close();
        }
    }

    @NotNull
    private static String getPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString() + "\\";
    }
}
