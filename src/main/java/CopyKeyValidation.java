import com.google.gson.JsonObject;
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
        if (args.length != 3) {
            System.out.println("Usage: Java -Jar " + CopyKeyValidation.class.getName() + ".jar <ENVIROMENT(INT,AMT,TST,PROD)>, <CopySource(20A,20B)>, <Output FileName>");
            System.exit(1);
        }

        String environment = args[0], release = args[1].toUpperCase(), outFileName = args[2] + "-" + args[0].toUpperCase() + "-" + args[1].toUpperCase() + "-" + ".xlsx";
        //  String environment = "int", release = "20B", outFileName = "output-sheet101.xlsx";

        String filePath = getPath() + "GOEP" + File.separator + "Online-R20B" + File.separator + "import" + File.separator;
        log.info(filePath);

        ArrayList<?> listOfLocales = inputLocaleFiles(filePath);
        for (Object localesXml : listOfLocales) {
            String market = localesXml.toString().substring(7, 12).toLowerCase().replace("-", "_");
            LinkedHashMap<String, String> xmlHMap = XmlReader.ReadXML(localesXml.toString(), filePath);
            JsonObject apiLabels = FetchAPIData.fetchLabels(createUrl(environment, market));
            ExcelOperations.checkFileIfExists(outFileName);
            compareXmlApi(xmlHMap, apiLabels, outFileName, market);
        }
    }

    private static void compareXmlApi(LinkedHashMap<String, String> hmap, JsonObject apiLabels, String outFileName, String market) throws IOException {
        log.info("Comparing XML Smartling Copy Label and API Copy Label response and adding difference Labels to excel file:" + outFileName);
        Set<?> set = hmap.entrySet();
        for (Object object : set) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
            String apiLabelQ = String.valueOf(apiLabels.get(entry.getKey().toString()));
            String apiLabel = "";

            if (!apiLabelQ.equals("null"))
                apiLabel = apiLabelQ.substring(1, apiLabelQ.length() - 1).replace("\\\"", "\"");

            if (!String.valueOf(entry.getValue()).equals(apiLabel)) {
                ExcelOperations.logToWorkbook(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()), apiLabel, outFileName, market);
                log.debug("key is: " + "\"" + entry.getKey() + "\"" + "\n" + "Value from Smartling XML file is : " + "\"" + entry.getValue() + "\"" + "\n" + "Value from api Request is: " + "\"" + apiLabel + "\"" + "\n");
            }
        }
        log.info("Logging for \"" + market.toUpperCase() + "\" made to excel file: " + outFileName);
    }

    private static String createUrl(String env, String market) {
        if (env.equalsIgnoreCase("prod")) env = "";
        else env += "-";
        return "https://" + env + "www2.hm.com/" + market + "/v1/labels";
    }

    private static ArrayList<String> inputLocaleFiles(String filePath) {
        File folder = new File(filePath);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> listOfFileNames = new ArrayList<>();
        assert listOfFiles != null;
        for (File listOfFile : listOfFiles)
            if (listOfFile.isFile())
                listOfFileNames.add(listOfFile.getName());
        log.info("List of filenames found for validation:" + listOfFileNames.toString());
        return listOfFileNames;
    }

    private static String getPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString() + File.separator;
    }
}



