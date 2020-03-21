import com.google.gson.JsonObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class CopyKeyValidation {
    private final static Logger log = LoggerFactory.getLogger(CopyKeyValidation.class);

    public static void main(String[] args) throws Exception {


        String environment = "prod", release = "20B", outFileName = "output-sheet101.xlsx";
        //String environment = args[0].toUpperCase(), release = args[1].toUpperCase(), inFileName = args[2];
        //String outFileName = inFileName + "-" + environment + "-" + release + "-" + ".xlsx";

        String filePath = getPath() + "GOEP" + File.separator + "Online-R20B" + File.separator + "import" + File.separator;
        log.info(filePath);

        ArrayList<String> listOfLocaleFiles = fetchLocaleFiles(filePath);
        for (String localesXml : listOfLocaleFiles) {
            String locale = localesXml.substring(7, 12).toLowerCase().replace("-", "_");

            LinkedHashMap<String, String> xmlHMap = XmlReader.readXML(localesXml, filePath);
            JsonObject apiLabels = new FetchAPIData().fetchLabels(createUrl(environment, locale));
            compareXmlAndLogOutputToFile(xmlHMap, apiLabels, outFileName, locale);
        }
    }

    private static void compareXmlAndLogOutputToFile(LinkedHashMap<String, String> hmap, JsonObject apiLabels, String outFileName, String locale) throws IOException {
        new ExcelOperations().createOutputFile(outFileName);
        log.info("Comparing XML Copy Label and API Copy Label response and adding difference Labels to excel file: {}", outFileName);
        Set<?> set = hmap.entrySet();
        for (Object o : set) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
            String apiLabelQ = apiLabels.get(entry.getKey().toString()) != null ? String.valueOf(apiLabels.get(entry.getKey().toString())) : null;
            String apiLabel = "";

            if (apiLabelQ != null)
                apiLabel = apiLabelQ.substring(1, apiLabelQ.length() - 1).replace("\\\"", "\"");

            if (isCopyKeyValueNotMatching(String.valueOf(entry.getValue()), apiLabel)) {
                ExcelOperations.logToWorkbook(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()), apiLabel, outFileName, locale);
                log.debug("key is: " + "\"" + entry.getKey() + "\"" + "\n" + "Value from Smartling XML file is : " + "\"" + entry.getValue() + "\"" + "\n" + "Value from api Request is: " + "\"" + apiLabel + "\"" + "\n");
            }
        }
        log.info("Logging for " + locale.toUpperCase() + " made to excel file: " + outFileName);
    }

    private static boolean isCopyKeyValueNotMatching(String expectedValue, String actualValue){

        return !expectedValue.equals(actualValue);

    }

    private static String createUrl(String env, String market) {
        switch (env) {
            case "int":
            case "amt":
            case "tst":
                env = env + "-";
                break;
            case "":
            case "prod":
                env = "";
                break;
        }
        return "https://" + env + "www2.hm.com/" + market + "/v1/labels";
    }

    private static ArrayList<String> fetchLocaleFiles(String filePath) {
        File folder = new File(filePath);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> listOfFileNames = new ArrayList<>();
        assert listOfFiles != null;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                assert false;
                listOfFileNames.add(listOfFile.getName());
                log.debug("File " + listOfFile.getName());
            } else if (listOfFile.isDirectory()) {
                log.debug("Directory " + listOfFile.getName());
            }
        }
        log.debug(listOfFileNames.toString());
        return listOfFileNames;
    }

    private static String getPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString() + File.separator;
    }
}



