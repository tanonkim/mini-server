package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * InputStream에서 HTTP 요청을 파싱합니다.
 * <p>
 * HTTP 요청 형식:
 * -----------------
 * GET /path?query=value HTTP/1.1    <- Request Line
 * Host: localhost:8080              <- Headers
 * Content-Type: application/json
 * <- 빈 줄 (헤더와 바디 구분)
 * {"key": "value"}                  <- Body (POST인 경우)
 * -----------------
 */
public class HttpRequest {

    private String method;
    private String path;
    private String httpVersion;
    private Map<String, String> queryParams;
    private Map<String, String> headers;
    private String body;

    public HttpRequest() {
        this.queryParams = new HashMap<>();
        this.headers = new HashMap<>();
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        HttpRequest request = new HttpRequest();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        );

        // 1. Parse Request Line
        String requestLine = reader.readLine();
        request.parseRequestLine(requestLine); // 쿼리스트링 파싱, "name=홍길동&age=25" → {name: "홍길동", age: "25"}

        // 2. Parse Header
        String headerLine;
        while ((headerLine = reader.readLine()) != null) {
            request.parseHeader(headerLine);
        }

        // 3. Parse Body
        String contentLength = request.getHeader("Content-Length");
        if (contentLength != null) {
            int length = Integer.parseInt(contentLength.trim());
            char[] bodyChars = new char[length];
            reader.read(bodyChars, 0, length);
            request.body = new String(bodyChars);
        }

        return request;

    }


    private void parseRequestLine(String requestLine) {
        String[] parts = requestLine.split(" ");
        // parts[0] = "GET"
        // parts[1] = "/users?name=홍길동&age=25"
        // parts[2] = "HTTP/1.1"

        method = parts[0];
        String pathWithQuery = parts[1];
        int questionDivision = pathWithQuery.indexOf("?");

        if (questionDivision != -1) { // 쿼리스트링 존재 /users?name=홍길동&age=25
            path = pathWithQuery.substring(0, questionDivision);
            String queryString = pathWithQuery.substring(questionDivision + 1);

            String[] splits = queryString.split("&");
            for (String split : splits) {
                String[] keyValue = split.split("=");
                queryParams.put(keyValue[0], keyValue[1]);
            }


        } else { // 쿼리스트링 부재 /users
            path = pathWithQuery;
        }
    }

    private void parseHeader(String headerLine) {
        int colon = headerLine.indexOf(":");

        String key = headerLine.substring(0, colon).trim();
        String value = headerLine.substring(colon + 1).trim();
        headers.put(key, value);
    }

    // Getter
    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    private String getHeader(String name) {
        return headers.get(name); // 무슨 코드인지 다시 확인
    }


    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", queryParams=" + queryParams +
                ", headers=" + headers +
                ", body='" + (body != null ? body.substring(0, Math.min(50, body.length())) + "..." : "null") + '\'' +
                '}';
    }
}
