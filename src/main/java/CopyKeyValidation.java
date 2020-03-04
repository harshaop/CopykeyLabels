import com.google.gson.JsonObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
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

    public static void main(String[] args) throws IOException, InvalidFormatException, ParserConfigurationException, SAXException {

        String environment = "prod";

        ArrayList<?> listOfLocales = test();
        for (Object localesXml : listOfLocales) {
            log.info(localesXml.toString());
            String market = localesXml.toString().substring(7, 12).toLowerCase().replace("-", "_");
            log.info(market);
            log.info(createUrl(environment, market));
            JsonObject apiLabels = FetchAPIData.fetchLabels(createUrl(environment, market));
            LinkedHashMap<String, String> hmap = xmlReader.ReadXML(localesXml.toString());
            ExcelOperations.checkFileStatus();
            copyValidation(hmap, apiLabels, environment, market);
        }
    }


    private static void copyValidation(LinkedHashMap<String, String> hmap, JsonObject apiLabels, String environment, String market) throws IOException, InvalidFormatException {
        log.info("Comparing XML COPY and Api COPY response and adding to excel file");
        Set<?> set = hmap.entrySet();
        for (Object o : set) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
            String apiLabelQ = String.valueOf(apiLabels.get(entry.getKey().toString()));
            String apiLabel = "";

            if (!apiLabelQ.equals("null"))
                apiLabel = apiLabelQ.substring(1, apiLabelQ.length() - 1).replace("\\\"", "\"");

            if (!String.valueOf(entry.getValue()).equals(apiLabel)) {
                ExcelOperations.logToWorkbook(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()), apiLabel, environment, market);
                log.info("key is: " + "\"" + entry.getKey() + "\"" + "\n" + "Value from Smartling XML file is : " + "\"" + entry.getValue() + "\"" + "\n" + "Value from api Request is: " + "\"" + apiLabel + "\"" + "\n");
            }
        }
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

    private static ArrayList<String> test() {
        String path = getPath();
        log.info(path);
        log.info(path + "GOEP\\Online-R20B\\import\\");

        File folder = new File(path + "GOEP\\Online-R20B\\import\\");
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> listOfFileNames = new ArrayList<>();
        assert listOfFiles != null;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                assert false;
                listOfFileNames.add(listOfFile.getName());
                log.debug(listOfFileNames.toString());
                log.debug("File " + listOfFile.getName());
            } else if (listOfFile.isDirectory()) {
                log.debug("Directory " + listOfFile.getName());
            }
        }
        log.debug(listOfFileNames.toString());
        return listOfFileNames;
    }

    @NotNull
    private static String getPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString() + "\\";
    }
}



