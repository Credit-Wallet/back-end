package vn.edu.iuh.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    SERVER_ERROR(500, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND(404, "Not found", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_REQUEST(400, "Invalid request", HttpStatus.BAD_REQUEST),
    BAD_REQUEST(400, "Bad request", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_FOUND(404, "Account not found", HttpStatus.NOT_FOUND),
    PASSWORD_NOT_MATCH(400, "Password not match", HttpStatus.BAD_REQUEST),
    ACCOUNT_EXISTED(400, "Account existed", HttpStatus.BAD_REQUEST),
    ALREADY_EXISTS(409, "Already exists", HttpStatus.CONFLICT);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
