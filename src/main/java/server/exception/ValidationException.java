package server.exception;

/**
 * 유효성 검사 실패 시 발생하는 예외
 * 
 * Spring의 MethodArgumentNotValidException 등의 역할을 합니다.
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
}
