import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FetchAPIData {
    private final static Logger log = LoggerFactory.getLogger(CopyKeyValidation.class);

    public static JsonObject fetchLabels(String url) throws Exception {
        log.info("Fetching Json data from API - " + url);

        System.setProperty("org.jboss.security.ignoreHttpsHost","true");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response responses = client.newCall(request).execute();
        String jsonData = Objects.requireNonNull(responses.body()).string();
        return new Gson().fromJson(jsonData, JsonObject.class);
    }
}

