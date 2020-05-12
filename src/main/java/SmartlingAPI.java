import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Objects;

import static org.apache.http.conn.ssl.SSLConnectionSocketFactory.SSL;

public class SmartlingAPI {
    private final static Logger log = LoggerFactory.getLogger(SmartlingAPI.class);

    @Test
    public void test() {
        RestAssured.useRelaxedHTTPSValidation();

        Response response = RestAssured.given()
                .auth()
                .oauth2("eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiIyMmY3MGYxZC00NWMwLTQyYzEtYTViMS00YjIxZjM5OWY1ZjEiLCJleHAiOjE1ODkxNDk4MTIsIm5iZiI6MCwiaWF0IjoxNTg5MTQ5MzMyLCJpc3MiOiJodHRwczovL3Nzby5zbWFydGxpbmcuY29tL2F1dGgvcmVhbG1zL1NtYXJ0bGluZyIsImF1ZCI6ImF1dGhlbnRpY2F0aW9uLXNlcnZpY2UiLCJzdWIiOiI2MDEzNjRiOS00YTZkLTQ3YzUtYTUxNC0yNGQ1Mzk0ZjZmNzUiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhdXRoZW50aWNhdGlvbi1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6IjZhN2UyMmEwLTQxMTktNGMyNS1hMmFmLWExNDAyNmIxNjlkYiIsImNsaWVudF9zZXNzaW9uIjoiYzA1NTkyMGYtNWUxZS00OTgzLTk1N2MtODZkMzNhOWE5MzE0IiwiYWxsb3dlZC1vcmlnaW5zIjpbXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIlJPTEVfQVBJX1VTRVIiLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsInZpZXctcHJvZmlsZSJdfX0sInVpZCI6ImVhOGYxNmE0YjUwOSIsIm5hbWUiOiJPTFAiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhcGlVc2VyK3Byb2plY3QrMjEzMzBhYWRhQHNtYXJ0bGluZy5jb20iLCJnaXZlbl9uYW1lIjoiQVBJIFVzZXIiLCJmYW1pbHlfbmFtZSI6Ik9MUCIsImVtYWlsIjoiYXBpVXNlcitwcm9qZWN0KzIxMzMwYWFkYUBzbWFydGxpbmcuY29tIn0.IxXAFSirlCPzU1O0jE36LEX8vO6RmUYeaLxkVS2BeMY-5Gex4vkg62akb3Yslk94S2UjJ35kV_rb0tUdQabCZzfAaxqs0l7skZbfXOulY6HNoNnERaSa0mnQAg2MuQh8tJJ9ezOKTIrPWqiEyWJiWVQq-2msw42izihXAr97yEM")
                .get("https://api.smartling.com/projects-api/v2/projects/21330aada");

        log.info(String.valueOf(response.statusCode()));
        log.info(String.valueOf(response.getBody().asString()));
        log.info(response.jsonPath().getString("accessToken"));
    }

    @Test(groups = "unitTest")
    void RestTestSmartlingAPI() {
        RestAssured.useRelaxedHTTPSValidation();
        Response accessResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body("{\"userIdentifier\":\"dcjdtoqkmefhofgpgvkbyybwzxvfbz\",\"userSecret\":\"egpa2n03r7sf6o6dgd21i6ddlnVf.m6vt7k904vkneaak3c15o0j9cl\"}")
                .post("https://api.smartling.com/auth-api/v2/authenticate");

        String jsonData = Objects.requireNonNull(accessResponse.body().asString());
        JsonObject jsonObjectToken = new Gson().fromJson(jsonData, JsonObject.class);

        String token = jsonObjectToken.get("response").getAsJsonObject().get("data").getAsJsonObject().get("accessToken").getAsString();

        String url = "https://api.smartling.com/files-api/v2/projects/4c3d73789/locales/sv-se/file";
        String url2 = "https://api.smartling.com/locales-api/v2/dictionary/locales";
        Response response = RestAssured
                .given()
                .auth()
                .oauth2(token)
                .queryParam("fileUri", "/GOEP/Online-R20C/import/labels.xml")
                .get(url);

        System.out.println(response.getStatusCode());
    }

    @Test
    public void testOk() throws IOException, KeyManagementException, NoSuchAlgorithmException {

        RestAssured.useRelaxedHTTPSValidation();
        Response accessResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body("{\"userIdentifier\":\"dcjdtoqkmefhofgpgvkbyybwzxvfbz\",\"userSecret\":\"egpa2n03r7sf6o6dgd21i6ddlnVf.m6vt7k904vkneaak3c15o0j9cl\"}")
                .post("https://api.smartling.com/auth-api/v2/authenticate");

        String jsonDataToken = Objects.requireNonNull(accessResponse.body().asString());
        JsonObject jsonObjectToken = new Gson().fromJson(jsonDataToken, JsonObject.class);
        String token = jsonObjectToken.get("response").getAsJsonObject().get("data").getAsJsonObject().get("accessToken").getAsString();

        String url = "https://api.smartling.com/files-api/v2/projects/4c3d73789/locales/sv-se/file?fileUri=/GOEP/Online-R20C/import/labels.xml";

        //        RequestBody formBody = new FormBody.Builder()
//                .add("userIdentifier", "fdcudgusoigdjefuzzqguewwurejel")
//                .add("userSecret", "55sr4np3vrc5qc4nnc7uj6lkn1Na!o3dvvhar3fp03cevdci5lipqv0")
//                .build();
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

        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + token)
                .url(url)
                .build();

        okhttp3.Response responses = client.newCall(request).execute();
        String jsonData = Objects.requireNonNull(responses.body()).string();
        System.out.println(jsonData);

    }

}
