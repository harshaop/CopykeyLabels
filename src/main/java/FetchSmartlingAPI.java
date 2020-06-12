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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.net.ssl.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class FetchSmartlingAPI {
    private final static Logger log = LoggerFactory.getLogger(FetchSmartlingAPI.class);

    public static LinkedHashMap<String, String> fetchXML(String locale, String release) throws Exception {
        log.info("Reading XML File from API: " + locale);

        Document doc = loadXMLFromString(fetchSmartlingAPI(locale, release));
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("translation");
        LinkedHashMap<String, String> hMap = new LinkedHashMap<>();
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                if (eElement.getAttribute("system").equals("hybris") & eElement.getAttribute("subsystem").equals("frontend")) {
                    log.debug("key : " + eElement.getAttribute("key"));
                    log.debug("Translation : " + eElement.getTextContent());
                    String translation = eElement.getTextContent().replaceAll("[\r\n]", "");
                    hMap.put(eElement.getAttribute("key"), translation);
                }
            }
        }
        return hMap;
    }

    public static String fetchSmartlingAPI(String locale, String release) throws IOException {
        String url = "https://api.smartling.com/files-api/v2/projects/4c3d73789/locales/" + locale.toLowerCase() + "/file?fileUri=/GOEP/Online-R" + release.toUpperCase() + "/import/labels.xml";

        OkHttpClient client = getUnsafeOkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getToken())
                .url(url)
                .build();

        okhttp3.Response responses = client.newCall(request).execute();
        String jsonData = Objects.requireNonNull(responses.body().string());
        System.out.println(responses.code());
        return jsonData;

    }

    public static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    private static String getToken() throws IOException {
        String urlAuthenticate = "https://api.smartling.com/auth-api/v2/authenticate";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userIdentifier", "fdcudgusoigdjefuzzqguewwurejel");
        jsonObject.put("userSecret", "55sr4np3vrc5qc4nnc7uj6lkn1Na!o3dvvhar3fp03cevdci5lipqv0");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        OkHttpClient client2 = getUnsafeOkHttpClient();
        Request accessRequest = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url(urlAuthenticate)
                .post(body)
                .build();
        okhttp3.Response accessResponse = client2.newCall(accessRequest).execute();

        String jsonDataToken = Objects.requireNonNull(accessResponse.body()).string();
        JsonObject jsonObjectToken = new Gson().fromJson(jsonDataToken, JsonObject.class);
        return jsonObjectToken.get("response").getAsJsonObject().get("data").getAsJsonObject().get("accessToken").getAsString();
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<String> getLoaclesList() throws IOException {
        RestAssured.useRelaxedHTTPSValidation();

        String url = "https://api.smartling.com/projects-api/v2/projects/4c3d73789";

        Response response = RestAssured
                .given()
                .auth()
                .oauth2(getToken())
                .get(url);

        String jsonData = Objects.requireNonNull(response.body()).asString();
        JsonObject jsonObject = new Gson().fromJson(jsonData, JsonObject.class);

        JsonArray locales = jsonObject.get("response").getAsJsonObject().get("data").getAsJsonObject().get("targetLocales")
                .getAsJsonArray();
        ArrayList<String> localesList = new ArrayList<>(), localesDescription = new ArrayList<>();
        for (JsonElement locale : locales) {
            localesList.add(locale.getAsJsonObject().get("localeId").getAsString());
            localesDescription.add(locale.getAsJsonObject().get("description").getAsString());
            //  System.out.print(locale.getAsJsonObject().get("localeId").getAsString() + " ");
            //System.out.print(locale.getAsJsonObject().get("description").getAsString() + "\t|\t");
        }
        System.out.print("\n" + localesList.size());
        return localesList;
    }
}

