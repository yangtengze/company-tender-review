package cn.edu.sdua._db.ytz.company_tender_review.common;

import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;
// 全局异常处理 GlobalExceptionHandler：统一返回业务错误码（如参数校验失败、请求体格式错误等）

@Slf4j
@RestControllerAdvice // 组合注解，相当于 @ControllerAdvice + @ResponseBody，
public class GlobalExceptionHandler {

    // 参数校验失败
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return R.fail(4001, message);
    }
    // 约束校验失败
    @ExceptionHandler(ConstraintViolationException.class)
    public R<Void> handleConstraintViolation(ConstraintViolationException ex) {
        return R.fail(4002, ex.getMessage());
    }
    // 请求体格式错误
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Void> handleUnreadable(HttpMessageNotReadableException ex) {
        return R.fail(4003, "request body invalid");
    }
    // 非法参数异常
    @ExceptionHandler(IllegalArgumentException.class)
    public R<Void> handleIllegalArgument(IllegalArgumentException ex) {
        return R.fail(4004, ex.getMessage());
    }
    // 通用异常
    @ExceptionHandler(Exception.class)
    public R<Void> handleGeneral(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return R.fail(5000, "internal server error");
    }
}
