/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.exception.handler;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.exception.EntityNotFoundException;
import me.zhengjie.utils.ThrowableUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Objects;
import static org.springframework.http.HttpStatus.*;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Slf4j
// @RestControllerAdvice https://www.jianshu.com/p/47aeeba6414c
// @RestControllerAdvice和ControllerAdvice区别 https://www.cnblogs.com/liaojie970/p/8502653.html
// 默认捕获全局抛出的异常
// RestControllerAdvice会自动帮助catch异常,并匹配如下相应的@ExceptionHandler,然后重新封装异常信息，返回值，统一格式(转为json)返回给前端。
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有不可知的异常
     */
    // 是通过e.getMessage()来构造一个类对象(类工厂模式)，而不是new出来，set进去，再将对象放进去。
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleException(Throwable e){
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        return buildResponseEntity(ApiError.error(e.getMessage()));
    }

    /**
     * BadCredentialsException-坏的凭证异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> badCredentialsException(BadCredentialsException e){
        // 打印堆栈信息
        String message = "坏的凭证".equals(e.getMessage()) ? "用户名或密码不正确" : e.getMessage();
        log.error(message);
        return buildResponseEntity(ApiError.error(message));
    }

    /**
     * 处理自定义异常（fixme：为什么没有BadConfigurationException呢？其他的自定义异常都有）
     */
	@ExceptionHandler(value = BadRequestException.class)
	public ResponseEntity<ApiError> badRequestException(BadRequestException e) {
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        return buildResponseEntity(ApiError.error(e.getStatus(),e.getMessage()));
	}

    /**
     * 处理 EntityExist
     */
    @ExceptionHandler(value = EntityExistException.class)
    public ResponseEntity<ApiError> entityExistException(EntityExistException e) {
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        return buildResponseEntity(ApiError.error(e.getMessage()));
    }

    /**
     * 处理 EntityNotFound
     */
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ApiError> entityNotFoundException(EntityNotFoundException e) {
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        return buildResponseEntity(ApiError.error(NOT_FOUND.value(),e.getMessage()));
    }

    /**
     * 处理所有接口数据验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        String[] str = Objects.requireNonNull(e.getBindingResult().getAllErrors().get(0).getCodes())[1].split("\\.");
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        String msg = "不能为空";
        if(msg.equals(message)){
            message = str[1] + ":" + message;
        }
        return buildResponseEntity(ApiError.error(message));
    }

    /**
     * ResponseEntity https://blog.csdn.net/qq_43317193/article/details/100109136
     * 为什么使用ResponseEntity<>？因为继承了HttpEntity，可以添加HttpStatus状态码的HttpEntity的扩展类
     * 统一封装返回类；同时泛型限定后为ResponseEntity<ApiError>后，即该类返回值只能是ApiError类
     */
    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        // 将ApiError返回实体类 转换为 HttpStatus类
        return new ResponseEntity<>(apiError, HttpStatus.valueOf(apiError.getStatus()));
    }
}
