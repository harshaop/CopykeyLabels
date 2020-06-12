package java.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

import static org.apache.http.conn.ssl.SSLConnectionSocketFactory.SSL;

public class SmartlingAPI {
    private final static Logger log = LoggerFactory.getLogger(SmartlingAPI.class);

    private static String createUrl(String env, String market) {
        market = market.replace("-","_").toLowerCase();
        if (env.equalsIgnoreCase("prod")) env = "";
        else env += "-";
        return "https://" + env + "www2.hm.com/" + market + "/v1/labels";
    }

    @Test
    public void test() {
        RestAssured.useRelaxedHTTPSValidation();

        Response response = RestAssured.given()
                .auth()
                .oauth2("eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJkZDRkZmE0Zi0zZWRhLTRiZDQtOTQ1OC1jNWM1NzEwYjlmYzUiLCJleHAiOjE1OTEyNzg3MjQsIm5iZiI6MCwiaWF0IjoxNTkxMjc4MjQ0LCJpc3MiOiJodHRwczovL3Nzby5zbWFydGxpbmcuY29tL2F1dGgvcmVhbG1zL1NtYXJ0bGluZyIsImF1ZCI6ImF1dGhlbnRpY2F0aW9uLXNlcnZpY2UiLCJzdWIiOiJkNWE4YTVmMS05MGM4LTQ1YzAtYjM5YS00Yzc2ZWVmN2RiYzAiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhdXRoZW50aWNhdGlvbi1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6ImI1NWFkMjYwLTc5MDUtNDgzMi04YWQyLTFhN2YyMmRkZjVmNCIsImNsaWVudF9zZXNzaW9uIjoiNTMwNWQ2ODEtNDM0NS00MDgwLWEwZDQtMWFjMTQ1OGIyMzFjIiwiYWxsb3dlZC1vcmlnaW5zIjpbXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIlJPTEVfQVBJX1VTRVIiLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsInZpZXctcHJvZmlsZSJdfX0sInVpZCI6ImQ0ZDIzMTNjZjU2NCIsIm5hbWUiOiJPbmxpbmVMYWJlbHNQcm9kIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiYXBpVXNlcitwcm9qZWN0KzRjM2Q3Mzc4OUBzbWFydGxpbmcuY29tIiwiZ2l2ZW5fbmFtZSI6IkFQSSBVc2VyIiwiZmFtaWx5X25hbWUiOiJPbmxpbmVMYWJlbHNQcm9kIiwiZW1haWwiOiJhcGlVc2VyK3Byb2plY3QrNGMzZDczNzg5QHNtYXJ0bGluZy5jb20ifQ.Rzpj-jonBuR9uQx0KJXaBWHFd4dsE7M6y5VCadRq_g9OAoOeTJKsyZLFq7eGqpH0WY9dIb93nL62ijfiHvT2iu4Si9y31se7K_GmxfCtw_f8w3VfNgFKg13xl91X_N2RgRmdgIE1pqaLpUPDwdrpJ1B7nx1Ys-8Ugp2iUOXKrfs")
                .get("https://api.smartling.com/projects-api/v2/projects/4c3d73789");

        log.info(String.valueOf(response.statusCode()));
        log.info(String.valueOf(response.getBody().asString()));
        log.info(response.jsonPath().getString("accessToken"));
    }

    @Test
    public void fetchLocales() {
        RestAssured.useRelaxedHTTPSValidation();
        Response accessResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body("{\"userIdentifier\":\"fdcudgusoigdjefuzzqguewwurejel\",\"userSecret\":\"55sr4np3vrc5qc4nnc7uj6lkn1Na!o3dvvhar3fp03cevdci5lipqv0\"}")
                .post("https://api.smartling.com/auth-api/v2/authenticate");

        String jsonDataAccessToken = Objects.requireNonNull(accessResponse.body().asString());
        JsonObject jsonObjectToken = new Gson().fromJson(jsonDataAccessToken, JsonObject.class);

        String token = jsonObjectToken.get("response").getAsJsonObject().get("data").getAsJsonObject().get("accessToken").getAsString();

        String url = "https://api.smartling.com/projects-api/v2/projects/4c3d73789";

        Response response = RestAssured
                .given()
                .auth()
                .oauth2(token)
                .get(url);

        String jsonData = Objects.requireNonNull(response.body()).asString();
        JsonObject jsonObject = new Gson().fromJson(jsonData, JsonObject.class);

        JsonArray locales = jsonObject.get("response").getAsJsonObject().get("data").getAsJsonObject().get("targetLocales")
                .getAsJsonArray();
        ArrayList<String> localesList = new ArrayList<>(),localesDescription = new ArrayList<>();
        for (JsonElement locale : locales) {
            localesList.add(locale.getAsJsonObject().get("localeId").getAsString());
            localesDescription.add(locale.getAsJsonObject().get("description").getAsString());
            System.out.print(locale.getAsJsonObject().get("localeId").getAsString() + " ");
            System.out.print(locale.getAsJsonObject().get("description").getAsString() + "\t|\t");
        }
        System.out.print("\n" + localesList.size());

    }

    @Test
    public void RestTestSmartlingAPI() {
        RestAssured.useRelaxedHTTPSValidation();
        Response accessResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body("{\"userIdentifier\":\"fdcudgusoigdjefuzzqguewwurejel\",\"userSecret\":\"55sr4np3vrc5qc4nnc7uj6lkn1Na!o3dvvhar3fp03cevdci5lipqv0\"}")
                .post("https://api.smartling.com/auth-api/v2/authenticate");

        String jsonData = Objects.requireNonNull(accessResponse.body().asString());
        JsonObject jsonObjectToken = new Gson().fromJson(jsonData, JsonObject.class);

        String token = jsonObjectToken.get("response").getAsJsonObject().get("data").getAsJsonObject().get("accessToken").getAsString();

        String url = "https://api.smartling.com/files-api/v2/projects/4c3d73789/locales/sv-se/file";
        Response response = RestAssured
                .given()
                .auth()
                .oauth2(token)
                .queryParam("fileUri", "/GOEP/Online-R20C/import/labels.xml")
                .get(url);

        System.out.println(response.getStatusCode());
        System.out.println(response.getBody().asString());
    }

    @Test
    public void testOk() throws IOException, KeyManagementException, NoSuchAlgorithmException {

        final SSLContext sslContext = SSLContext.getInstance(SSL);
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {

                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                   String authType) throws
                            CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                   String authType) throws
                            CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userIdentifier", "fdcudgusoigdjefuzzqguewwurejel");
        jsonObject.put("userSecret", "55sr4np3vrc5qc4nnc7uj6lkn1Na!o3dvvhar3fp03cevdci5lipqv0");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        OkHttpClient client2 = builder.build();
        Request accessRequest = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url("https://api.smartling.com/auth-api/v2/authenticate")
                .post(body)
                .build();
        okhttp3.Response accessResponse = client2.newCall(accessRequest).execute();

        /*RestAssured.useRelaxedHTTPSValidation();
        Response accessResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body("{\"userIdentifier\":\"fdcudgusoigdjefuzzqguewwurejel\",\"userSecret\":\"55sr4np3vrc5qc4nnc7uj6lkn1Na!o3dvvhar3fp03cevdci5lipqv0\"}")
                .post("https://api.smartling.com/auth-api/v2/authenticate");*/

        String jsonDataToken = accessResponse.body().string();
        JsonObject jsonObjectToken = new Gson().fromJson(jsonDataToken, JsonObject.class);
        String token = jsonObjectToken.get("response").getAsJsonObject().get("data").getAsJsonObject().get("accessToken").getAsString();
        String url = "https://api.smartling.com/files-api/v2/projects/4c3d73789/locales/sv-se/file?fileUri=/GOEP/Online-R20B/import/labels.xml";


        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + token)
                .url(url)
                .build();

        okhttp3.Response responses = client.newCall(request).execute();
        String jsonData = Objects.requireNonNull(responses.body()).string();
        System.out.println(jsonData);

    }

    @Test
    public void testcode() throws IOException {
        new FetchSmartlingAPI();
    }

    @Test
    public void feAPI() throws IOException {
        String url = "https://www2.hm.com/en_gb/v1/labels";


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        okhttp3.Response responses = client.newCall(request).execute();
        System.out.println(Objects.requireNonNull(responses.body()).string());
    }

    @Test
    public void testMainCode() throws Exception {
        String locale = "zh-TW";
        String environment = "prod";
        String release = "20B";
        String outFileName = "outputTestAll" + "-" + environment +"-" + ".xlsx";


//        for (String locale: FetchSmartlingAPI.getLoaclesList()){
            LinkedHashMap<String, String> xmlHMap = FetchSmartlingAPI.fetchXML(locale,release);
            JsonObject apiLabels = new FetchAPIData().fetchLabels(environment, locale);
            CopyKeyValidation.compareXmlAndLogOutputToFile(xmlHMap, apiLabels, outFileName, locale);
//        }
    }

    @Test
    public void tetJson() throws Exception {
        String outFileName = "outputTest" + "-" + "-" + ".xlsx";
        String localeLoc = "EN_GB";
        String environment = "prod";
        JsonObject apiLabels = new FetchAPIData().fetchLabels(environment, localeLoc);
    }
}

