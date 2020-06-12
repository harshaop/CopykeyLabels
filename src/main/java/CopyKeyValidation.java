import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class CopyKeyValidation {
    private final static Logger log = LoggerFactory.getLogger(CopyKeyValidation.class);

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: Java -Jar " + CopyKeyValidation.class.getName() + ".jar <ENVIRONMENT(INT,AMT,TST,PROD> <CopySource(20A,20B)> <Country-Locale>");
            System.exit(1);
        }

        String environment = args[0].toUpperCase(), release = args[1].toUpperCase(), localeIn = args[2];
        String outFileName = "output" + "-" + environment + "-" + release + "-" + localeIn + ".xlsx";

        log.info(localeIn);

        if (localeIn.toLowerCase().equals("all")) {
            for (String locale : FetchSmartlingAPI.getLoaclesList()) {
                LinkedHashMap<String, String> xmlHMap = FetchSmartlingAPI.fetchXML(locale, release);
                JsonObject apiLabels = new FetchAPIData().fetchLabels(environment, locale);
                CopyKeyValidation.compareXmlAndLogOutputToFile(xmlHMap, apiLabels, outFileName, locale);
            }
        } else {
            LinkedHashMap<String, String> xmlHMap = FetchSmartlingAPI.fetchXML(localeIn, release);
            JsonObject apiLabels = new FetchAPIData().fetchLabels(environment, localeIn);
            CopyKeyValidation.compareXmlAndLogOutputToFile(xmlHMap, apiLabels, outFileName, localeIn);
        }

    }

    static void compareXmlAndLogOutputToFile(LinkedHashMap<String, String> hmap, JsonObject apiLabels, String outFileName, String locale) throws IOException {
        if (!(apiLabels == null)) {
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
    }

    private static boolean isCopyKeyValueNotMatching(String expectedValue, String actualValue) {
        return !expectedValue.equals(actualValue);
    }

}



