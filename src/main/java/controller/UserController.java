package controller;

import controller.request.UserRequest;
import server.HttpRequest;
import server.HttpResponse;
import server.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 관련 요청을 처리하는 컨트롤러
 *
 * Spring의 @RestController + @RequestMapping("/users") 역할을 합니다.
 *
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
    private final Long idGenerator = 1L;

    /**
     * GET /users?name=xxx&age=xx
     *
     * Spring:
     * @GetMapping
     * public User getUser(@RequestParam String name, @RequestParam Integer age) { ... }
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


}
