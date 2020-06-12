import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public class FetchAPIData {
    private final static Logger log = LoggerFactory.getLogger(CopyKeyValidation.class);

    private static String createUrl(String env, String mkt) {
        String market = mkt.replace("-","_").toLowerCase();
        if (env.equalsIgnoreCase("prod")) env = "";
        else env += "-";
        return "https://" + env + "www2.hm.com/" + market + "/v1/labels";
    }

    public JsonObject fetchLabels(String env, String locale) throws Exception {
        String url = createUrl(env, locale);
        log.info("Fetching Json data from API - " + url);

        OkHttpClient client = FetchSmartlingAPI.getUnsafeOkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response responses = client.newCall(request).execute();
        //List<String> contentTypeHeader = responses.headers("Content-Type");
        if (responses.headers("Content-Type").get(0).equals("application/json;charset=UTF-8")) {
            String jsonData = Objects.requireNonNull(responses.body()).string();
            return new Gson().fromJson(jsonData, JsonObject.class);
        } else{
            System.out.println("Market " + locale + " is not working, no logging done for " + locale);
            return null;
        }
    }
}

