package server;

import java.net.Socket;

/**
 * HTTP 요청을 처리하는 핸들러
 *
 * Spring의 DispatcherServlet이 해주는 일을 직접 구현합니다.
 * - 요청 파싱
 * - URL 라우팅 (어떤 컨트롤러의 어떤 메서드를 호출할지 결정)
 * - 예외 처리
 * - 응답 전송
 */
public class RequestHandler {
    public void handle(Socket clientSocket) {
        HttpResponse httpResponse = new HttpResponse();
    }
}
