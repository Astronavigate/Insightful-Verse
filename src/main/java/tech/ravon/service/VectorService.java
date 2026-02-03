package tech.ravon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Service
public class VectorService {

    private static final String BASE_URL = "http://127.0.0.1:7824";
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 直接转发 updVector 请求
     */
    public void updVector(String tableId, String tableName, String content) {
        try {
            URL url = new URL(BASE_URL + "/updVector");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            Map<String, Object> payload = Map.of(
                    "table_id", tableId,
                    "table_name", tableName,
                    "content", content
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(objectMapper.writeValueAsBytes(payload));
            }

            int code = conn.getResponseCode();
            if (code != 200) {
                throw new RuntimeException("updVector failed with HTTP code: " + code);
            }

            Map<String, Object> resp = objectMapper.readValue(conn.getInputStream(), Map.class);
            resp.get("uuid");

        } catch (Exception e) {
            throw new RuntimeException("updVector exception: " + e.getMessage(), e);
        }
    }

    /**
     * 直接转发 delVector 请求
     */
    public void delVector(String tableId, String tableName) {
        try {
            URL url = new URL(BASE_URL + "/delVector");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            Map<String, Object> payload = Map.of(
                    "table_id", tableId,
                    "table_name", tableName
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(objectMapper.writeValueAsBytes(payload));
            }

            int code = conn.getResponseCode();
            if (code != 200) {
                throw new RuntimeException("delVector failed with HTTP code: " + code);
            }

            Map<String, Object> resp = objectMapper.readValue(conn.getInputStream(), Map.class);
            resp.get("uuid");

        } catch (Exception e) {
            throw new RuntimeException("delVector exception: " + e.getMessage(), e);
        }
    }
}