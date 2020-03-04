import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class FetchAPIData {
    private final static Logger log = LoggerFactory.getLogger(CopyKeyValidation.class);

    public static JsonObject fetchLabels(String url) throws IOException {
        log.info("Fetching Json data from API - " + url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response responses = client.newCall(request).execute();
        String jsonData = Objects.requireNonNull(responses.body()).string();
        return new Gson().fromJson(jsonData, JsonObject.class);
    }
}

