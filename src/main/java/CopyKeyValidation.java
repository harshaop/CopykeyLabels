import com.google.gson.JsonObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class CopyKeyValidation {
    private final static Logger log = LoggerFactory.getLogger(CopyKeyValidation.class);

    public static void main(String[] args) throws IOException, InvalidFormatException, ParserConfigurationException, SAXException {


        String environment = "prod";
        String market = "en_us";
//        String url = createUrl(args[0], args[1]);// "https://"+"www2.hm.com/en_gb/v1/labels";
        String url = createUrl(environment, market);// "https://"+"www2.hm.com/en_gb/v1/labels";


        JsonObject apiLabels = FetchAPIData.fetchLabels(url);
        LinkedHashMap hmap = xmlReader.ReadXML(market);
        ExcelOperations.checkFileStatus();

        log.info("Comparing XML COPY and Api COPY response and adding to excel file");
        Set set = hmap.entrySet();
        for (Object o : set) {
            Map.Entry entry = (Map.Entry) o;
            String apiLabelQ = String.valueOf(apiLabels.get(entry.getKey().toString()));
            String apiLabel = "";

            if (!apiLabelQ.equals("null"))
                apiLabel = apiLabelQ.substring(1, apiLabelQ.length() - 1).replace("\\\"", "\"");

            if (!String.valueOf(entry.getValue()).equals(apiLabel)) {
                ExcelOperations.modifyWorkbook(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()), apiLabel, environment, market);
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
}


