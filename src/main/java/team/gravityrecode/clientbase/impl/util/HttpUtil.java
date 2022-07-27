package team.gravityrecode.clientbase.impl.util;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HttpUtil {
    private final static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String USER_AGENT = "pulsabito";

    public static CompletableFuture<HttpResponse> asyncHttpsConnection(String url) {
        return asyncHttpConnection(url);
    }

    public static CompletableFuture<HttpResponse> asyncHttpsConnection(String url, Map<String, String> headers) {
        CompletableFuture<HttpResponse> toComplete = new CompletableFuture<>();
        executorService.submit(() -> {
            toComplete.complete(httpsConnection(url, headers));
        });
        return toComplete;
    }

    public static CompletableFuture<HttpResponse> asyncHttpConnection(String url) {
        return asyncHttpConnection(url, null);
    }

    public static CompletableFuture<HttpResponse> asyncHttpConnection(String url, Map<String, String> headers) {
        CompletableFuture<HttpResponse> toComplete = new CompletableFuture<>();
        executorService.submit(() -> {
            toComplete.complete(httpConnection(url, headers));
        });
        return toComplete;
    }
    public static class AllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
    public static HttpResponse httpsConnection(String url, Map<String, String> headers) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setHostnameVerifier(new AllHostnameVerifier());
            connection.setRequestProperty("User-Agent", USER_AGENT);
            if (headers != null)
                headers.forEach(connection::setRequestProperty);
            connection.connect();
            return new HttpResponse(inputStreamToString(connection.getInputStream()), connection.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HttpResponse httpConnection(String url, Map<String, String> headers) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            if (headers != null)
                headers.forEach(connection::setRequestProperty);
            connection.connect();
            return new HttpResponse(inputStreamToString(connection.getInputStream()), connection.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String inputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append(System.lineSeparator());
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    public static class HttpResponse {
        private final String content;
        private final int response;

        public HttpResponse(String content, int response) {
            this.content = content;
            this.response = response;
        }

        public String content() {
            return content;
        }

        public int response() {
            return response;
        }
    }
}
