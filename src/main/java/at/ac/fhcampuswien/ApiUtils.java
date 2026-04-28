package at.ac.fhcampuswien;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

//ue2:
// import map and hashmap so we can save the search parameters inside a list of keys and values
import java.util.Map;
import java.util.HashMap;
public class ApiUtils {
    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    //ue2:
    // helper method takes the query part of the web link
    // it splits it wherever there is & to separate different search terms
    // then it splits those terms by the equals sign to get the name and the value
    // it puts them all in a map and gives it back to us so we can search like links

    public static Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return map;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length > 1) {
                map.put(keyValue[0], keyValue[1]);
            } else {
                map.put(keyValue[0], "");
            }
        }
        return map;
    }
}