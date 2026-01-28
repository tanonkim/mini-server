package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers;
    private String body;

    public HttpResponse() {
        this.headers = new HashMap<>();
        this.headers.put("Content-Type", "application/json; charset=UTF-8");
    }


    public HttpResponse errorBody(String message) {
        this.body = "{\"error\": \"" + escapeJson(message) + "\"}";
        return this;
    }

    /**
     * JSON 문자열에서 특수문자 이스케이프
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public void write(OutputStream outputStream) throws IOException {
        StringBuilder response = new StringBuilder();

        // 1. Status Line
        response.append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(statusMessage)
                .append("\r\n");

        // Body의 바이트 길이 계산 (UTF-8 기준)
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        headers.put("Content-Length", String.valueOf(bodyBytes.length));

        // 2. Headers
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            response.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\r\n");
        }

        // 3. 빈 줄 (헤더와 바디 구분)
        response.append("\r\n");

        // 4. Body
        response.append(body);

        // 응답 전송
        outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();

        System.out.println("[응답 전송] " + statusCode + " " + statusMessage);

    }

    public HttpResponse badRequest() {
        this.statusCode = 400;
        this.statusMessage = "Bad Request";
        return this;
    }


    public HttpResponse notFound() {
        this.statusCode = 404;
        this.statusMessage = "Not Found";
        return this;
    }

    public HttpResponse internalServerError() {
        this.statusCode = 500;
        this.statusMessage = "Internal Server Error";
        return this;
    }

    public HttpResponse ok() {
        this.statusCode = 200;
        this.statusMessage = "OK";
        return this;
    }

    public HttpResponse body(String body) {
        this.body = body;
        return this;
    }


    public HttpResponse methodNotAllowed() {
        this.statusCode = 405;
        this.statusMessage = "Method Not Allowed";
        return this;
    }

    public HttpResponse created() {
        this.statusCode = 201;
        this.statusMessage = "Created";
        return this;
    }

    public HttpResponse header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }
}
