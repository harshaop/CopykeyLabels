
import com.google.gson.JsonObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


public class CopyKeyValidation {
    private final static Logger log = LoggerFactory.getLogger(CopyKeyValidation.class);

    public static void main(String[] args) throws IOException, InvalidFormatException {


        String url = createUrl(args[0], args[1]);// "https://"+"www2.hm.com/en_gb/v1/labels";
   //     String url = createUrl("prod", "en_gb");// "https://"+"www2.hm.com/en_gb/v1/labels";

        JsonObject apiLabels = FetchAPIData.fetchLabels(url);
        LinkedHashMap hmap = ExcelOperations.readExcel();
        ExcelOperations.checkFileStatus();

        log.info("Comparing Excel and Api response and adding to excel file");
        Set set = hmap.entrySet();
        for (Object o : set) {
            Map.Entry entry = (Map.Entry) o;
            String apiLabelQ = String.valueOf(apiLabels.get(entry.getKey().toString()));
            String apiLabel = "NULL-Key Not Present in API";
            if (!apiLabelQ.equals("null"))
                apiLabel = apiLabelQ.substring(1, apiLabelQ.length() - 1);

            if (!String.valueOf(entry.getValue()).equals(apiLabel)) {
                ExcelOperations.modifyWorkbook(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()), apiLabel);
                log.debug("key is: " + "\"" + entry.getKey() + "\"" + "\n" + "Value from excel file is : " + "\"" + entry.getValue() + "\"" + "\n" + "Value from api Request is: " + "\"" + apiLabel + "\"" + "\n");
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


