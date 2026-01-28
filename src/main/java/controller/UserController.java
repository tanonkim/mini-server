package controller;

import controller.request.UserRequest;
import server.HttpRequest;
import server.HttpResponse;
import server.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 관련 요청을 처리하는 컨트롤러
 * <p>
 * Spring의 @RestController + @RequestMapping("/users") 역할을 합니다.
 * <p>
 * Spring에서:
 * - @GetMapping, @PostMapping으로 라우팅 자동 처리
 * - @RequestParam으로 쿼리 파라미터 자동 바인딩
 * - @RequestBody로 JSON 자동 파싱 및 객체 변환
 * - @Valid로 유효성 검사 자동 처리
 * - ResponseEntity로 응답 쉽게 생성
 */
public class UserController {

    // DB 대신 인메모리 저장
    private final List<UserRequest> users = new ArrayList<>();
    private long idGenerator = 1L;

    /**
     * GET /users?name=xxx&age=xx
     * <p>
     * Spring:
     *
     * @GetMapping public User getUser(@RequestParam String name, @RequestParam Integer age) { ... }
     */

    public void getUser(HttpRequest request, HttpResponse response) {
        // 1. 요청 파라미터 추출 (@RequestParam)
        String name = request.getQueryParam("name");
        String ageStr = request.getQueryParam("age");

        // 2. 유효성 검사 (@Valid, @NotNull)

        // 3. 타입변환
        int age = parseAge(ageStr);

        // 4. 비즈니스 로직 구현 필요
        UserRequest user = new UserRequest(null, name, age);

        // 5. 응답 생성 (Spring의 @ResponseBody, Jackson 역할)
        String jsonResponse = String.format(
                "{\"message\": \"사용자 조회 성공\", \"user\": {\"name\": \"%s\", \"age\": %d}}",
                escapeJson(name), age
        );

        response.ok().body(jsonResponse);

    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private int parseAge(String ageStr) {
        try {
            int age = Integer.parseInt(ageStr.trim());
            if (age < 0 || age > 150) {
                throw new ValidationException("age는 0에서 150 사이여야 합니다. 입력값: " + age);
            }
            return age;
        } catch (NumberFormatException e) {
            throw new ValidationException("age는 숫자여야 합니다. 입력값: " + ageStr);
        }
    }

    private void validateGetUserParams(String name, String ageStr) {
        ArrayList<String> errors = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            errors.add("name is required");
        }
        if (ageStr == null || ageStr.isEmpty()) {
            errors.add("age is required");
        }
        if (name != null && name.length() > 30) {
            errors.add("name " + name + " is too long");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
    }


    public void createUser(HttpRequest request, HttpResponse response) {
        // 1. Content-Type 검사
        String contentType = request.getHeader("Content-Type");
        if (contentType == null || !contentType.contains("application/json")) {
            throw new ValidationException("Content-Type은 application/json이어야 합니다.");
        }

        // 2. Body에서 JSON 파싱 (Spring + Jackson 역할)
        String body = request.getBody();
        if (body == null || body.trim().isEmpty()) {
            throw new ValidationException("요청 본문이 비어있습니다.");
        }

        // 간단한 JSON 파싱 (실제로는 Jackson 같은 라이브러리 사용)
        String name = extractJsonString(body, "name");
        Integer age = extractJsonInteger(body, "age");

        // 3. 유효성 검사 (추후 서비스 로직 말고 DTO에서 진행)
        validateCreateUserParams(name, age);

        // 4. 비즈니스 로직 - 사용자 생성
        UserRequest newUser = new UserRequest(idGenerator++, name, age);
        users.add(newUser);

        System.out.println("[사용자 생성] " + newUser);

        // 5. 응답 생성 (201 Created)
        String jsonResponse = String.format(
                "{\"message\": \"사용자 생성 성공\", \"user\": {\"id\": %d, \"name\": \"%s\", \"age\": %d}}",
                newUser.getId(), escapeJson(newUser.getName()), newUser.getAge()
        );

        response.created()
                .header("Location", "/users/" + newUser.getId())
                .body(jsonResponse);

    }


    private String extractJsonString(String json, String key) {
        // "key": "value" 패턴 찾기
        String pattern = "\"" + key + "\"";
        int keyIndex = json.indexOf(pattern);
        if (keyIndex == -1) {
            return null;
        }

        int colonIndex = json.indexOf(':', keyIndex);
        if (colonIndex == -1) {
            return null;
        }

        int valueStart = json.indexOf('"', colonIndex);
        if (valueStart == -1) {
            return null;
        }

        int valueEnd = json.indexOf('"', valueStart + 1);
        if (valueEnd == -1) {
            return null;
        }

        return json.substring(valueStart + 1, valueEnd);

    }

    private Integer extractJsonInteger(String json, String key) {
        String pattern = "\"" + key + "\"";
        int keyIndex = json.indexOf(pattern);
        if (keyIndex == -1) {
            return null;
        }

        int colonIndex = json.indexOf(':', keyIndex);
        if (colonIndex == -1) {
            return null;
        }

        // 숫자 시작점 찾기
        int valueStart = colonIndex + 1;
        while (valueStart < json.length() &&
                (json.charAt(valueStart) == ' ' || json.charAt(valueStart) == '\t')) {
            valueStart++;
        }

        // 숫자 끝점 찾기
        int valueEnd = valueStart;
        while (valueEnd < json.length() &&
                (Character.isDigit(json.charAt(valueEnd)) || json.charAt(valueEnd) == '-')) {
            valueEnd++;
        }

        if (valueStart == valueEnd) {
            return null;
        }

        try {
            return Integer.parseInt(json.substring(valueStart, valueEnd));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void validateCreateUserParams(String name, Integer age) {
        List<String> errors = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            errors.add("name is required");
        }

        if (age == null) {
            errors.add("age is required");
        }

        if (name != null && name.length() > 50) {
            errors.add("name " + name + " is too long");
        }

        if (age != null && (age < 0 || age > 150)) {
            errors.add("age " + age + " is too long");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
    }
}
