package com.ecommerce.handler;

import com.ecommerce.common.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * API 오류 응답의 공통 형태
 * errors는 @Valid 검증 실패일 때만 채워진다
 */
@Getter
@JsonPropertyOrder({"timestamp", "status", "code", "message", "errors"})
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String code;
    private final String message;

    // 비어 있으면 JSON에서 키 자체가 빠진다
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<ValidationError> errors;

    private ErrorResponse(ErrorCode errorCode, List<ValidationError> errors) {
        this.status = errorCode.getStatus().value();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errors = errors;
    }

    /**
     * 필드 단위 상세가 없는 일반 예외 응답을 생성한다
     */
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode, Collections.emptyList());
    }

    /**
     * @Valid 검증 실패 응답을 생성한다
     *
     * @param bindingResult MethodArgumentNotValidException이 들고 있는 검증 결과
     */
    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
        return new ErrorResponse(errorCode, ValidationError.from(bindingResult));
    }

    /**
     * 검증에 실패한 필드 하나의 정보
     * 클라이언트가 보낸 값(rejectedValue)은 password 같은 민감값이 섞이므로 담지 않는다
     */
    @Getter
    @JsonPropertyOrder({"field", "reason"})
    public static class ValidationError {

        private final String field;
        private final String reason;

        private ValidationError(FieldError fieldError) {
            this.field = fieldError.getField();
            this.reason = fieldError.getDefaultMessage();
        }

        private static List<ValidationError> from(BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(ValidationError::new)
                    .toList();
        }
    }
}
