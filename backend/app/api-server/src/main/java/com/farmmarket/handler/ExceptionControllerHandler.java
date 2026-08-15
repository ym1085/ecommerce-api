package com.farmmarket.handler;

import com.farmmarket.common.enums.ErrorCode;
import com.farmmarket.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerHandler {

    /**
     * Service에서 던진 비즈니스 예외를 ErrorCode의 상태 코드로 변환한다
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("Business exception: code={}, message={}", errorCode.getCode(), e.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    /**
     * @RequestBody @Valid 검증 실패를 처리한다
     * 예외가 들고 있는 BindingResult에서 필드별 실패 사유를 꺼내 errors에 담는다
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn("Validation failed: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode, e.getBindingResult()));
    }

    /**
     * 요청 본문을 객체로 변환하지 못했을 때 처리한다 (JSON 문법 오류, 타입 불일치, 빈 본문)
     * 이 단계에서 실패하면 @Valid까지 가지 못하므로, 없으면 클라이언트 잘못이 500으로 나간다
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        // 예외 메시지에 본문 원문이 섞이므로 로그에만 남긴다
        log.warn("Request body not readable: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST_BODY;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    /**
     * 위에서 잡지 못한 모든 예외를 500으로 처리한다
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Internal server error: {}", e.getMessage(), e);
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }
}
