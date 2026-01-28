package server;

import controller.UserController;
import server.exception.NotFoundException;
import server.exception.ValidationException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * HTTP 요청을 처리하는 핸들러
 * <p>
 * Spring의 DispatcherServlet이 해주는 일을 직접 구현합니다.
 * - 요청 파싱
 * - URL 라우팅 (어떤 컨트롤러의 어떤 메서드를 호출할지 결정)
 * - 예외 처리
 * - 응답 전송
 */
public class RequestHandler {

    private final UserController userController;

    public RequestHandler() {
        this.userController = new UserController();
    }

    public void handle(Socket clientSocket) {
        HttpResponse response = new HttpResponse();

        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()
        ) {
            // 1. HTTP 요청 파싱
            HttpRequest request;
            try {
                request = HttpRequest.parse(inputStream);
            } catch (Exception e) {
                response.badRequest().errorBody("요청 파싱 실패");
                response.write(outputStream);
                return;
            }

            // 2. URL 라우팅 및 컨트롤러 호출
            try {
                route(request, response);
            } catch (ValidationException e) {
                // 유효성 검사 실패
                response.badRequest()
                        .errorBody(e.getMessage());
            } catch (NotFoundException e) {
                // 리소스를 찾을 수 없음
                response.notFound()
                        .errorBody(e.getMessage());
            } catch (Exception e) {
                // 그 외 예외
                System.err.println("요청 처리 중 오류: " + e.getMessage());
                e.printStackTrace();
                response.internalServerError()
                        .errorBody("서버 내부 오류가 발생했습니다.");
            }

            // 3. 응답 전송
            response.write(outputStream);

        } catch (Exception e) {
            System.err.println("소켓 통신 오류: " + e.getMessage());
        }
    }

    /**
     * URL 경로에 따라 적절한 컨트롤러 메서드로 라우팅
     * <p>
     * Spring MVC에서는 @RequestMapping 어노테이션으로 자동 처리되지만,
     * 여기서는 직접 if-else로 라우팅해야 한다.
     */
    private void route(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        String method = request.getMethod();

    }
}
