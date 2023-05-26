package com.training.spring.aop;

import javax.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.training.jpa.vo.MemberInfo;



@Aspect
@Component
public class MemberAspect {
    private static final Logger logger = LoggerFactory.getLogger(MemberAspect.class);

    @Autowired
    private HttpSession httpSession;

    @Around("@annotation(MemberLoginCheck)")
    public Object checkLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("HttpSession checkLogin:" + httpSession.getId());
        MemberInfo sessionMemberInfo = (MemberInfo) httpSession.getAttribute("sessionMemberInfo");
        if (sessionMemberInfo != null) {
            // 使用者已登入，放行
            return joinPoint.proceed();
        } else {
            // 使用者未登入，導向登入頁面
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
