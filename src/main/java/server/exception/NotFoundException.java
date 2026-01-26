package server.exception;

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 * 
 * Spring의 404 Not Found 처리 역할을 합니다.
 */
public class NotFoundException extends RuntimeException {
    
    public NotFoundException(String message) {
        super(message);
    }
}
