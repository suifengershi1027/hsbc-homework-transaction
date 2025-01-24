package com.hsbc.management.exception;

import com.hsbc.management.common.BaseResult;
import com.hsbc.management.common.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param e 抛出的业务异常
     * @return 包含错误信息的BaseResult对象
     */
    @ExceptionHandler(value = {BizException.class})
    public BaseResult bizError(Exception e) {
        String msg = e.getMessage();
        log.error("biz error = {}", msg);
        return BaseResult.fail(ResultCodeEnum.BIZ_ERROR.getCode(), msg);
    }

    /**
     * 全局异常处理
     *
     * @param e 抛出的异常
     * @return 包含错误信息的BaseResult对象
     */
    @ExceptionHandler({Throwable.class})
    public BaseResult error(Throwable e) {
        log.error("errorMsg = {}", e.getMessage(), e);
        String msg = StringUtils.isEmpty(e.getMessage()) ? ResultCodeEnum.SYSTEM_ERROR.getMsg() : e.getMessage();
        return BaseResult.fail(ResultCodeEnum.BIZ_ERROR.getCode(), msg);
    }

    /**
     * 处理参数验证异常
     *
     * @param ex 抛出的MethodArgumentNotValidException异常
     * @return 包含错误信息的BaseResult对象
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResult handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        // 创建一个Map用于存储错误信息
        Map<String, String> errors = new HashMap<>();
        // 遍历所有的错误信息
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return BaseResult.fail(ResultCodeEnum.PARAM_ERROR.getCode(), errors.toString());
    }
}
