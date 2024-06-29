package com.sky.aspect;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类
 */

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    //指定切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..))&& @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    //前置通知
    @Before("autoFillPointCut()")
    public void antoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充...");
        //获取操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();
        //获取被拦截的方法的的参数-实体对象
        Object[] args = joinPoint.getArgs();
        if(args==null||args.length==0)return;
        Object entity=args[0];
        //准备赋值的对象

        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        //根据类型通过反射赋值
        if(operationType==OperationType.INSERT){
            try {
                //获取方法
                Method setCreateTime = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射赋值
                setCreateTime.invoke(entity,now);
                setUpdateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        else if (operationType==OperationType.UPDATE){
            try {
                //获取方法
                Method setUpdateTime = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass()
                        .getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
