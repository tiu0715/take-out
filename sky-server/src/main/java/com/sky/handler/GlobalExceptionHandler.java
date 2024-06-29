package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.exception.PasswordEditFailedException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 员工添加重复
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //Duplicate entry 'lubenwei' for key 'employee.idx_username'
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String username=split[2];
            String msg=username+MessageConstant.ALREADY_EXISTS;
            return Result.error(msg);
        }
        else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

    /**
     * 员工密码修改错误异常
     * @param ex
     * @return
     */
    public Result exceptionHandler(PasswordEditFailedException ex){
        return Result.error(MessageConstant.PASSWORD_EDIT_FAILED);
    }


}
