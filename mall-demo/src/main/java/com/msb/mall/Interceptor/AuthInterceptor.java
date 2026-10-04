package com.msb.mall.Interceptor;

import com.msb.common.constant.AuthConstant;
import com.msb.common.vo.MemberVO;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 我们自定义的拦截器：帮助我们获取当前登录的用户信息
 *     通过Session共享获取的
 */
public class AuthInterceptor implements HandlerInterceptor {
    private final boolean demoEnabled;

    public AuthInterceptor() { this(false); }

    public AuthInterceptor(boolean demoEnabled) { this.demoEnabled = demoEnabled; }
    // 本地线程对象  Map<thread,Object>
    public static ThreadLocal<MemberVO> threadLocal = new ThreadLocal();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        threadLocal.remove();
        // 通过HttpSession获取当前登录的用户信息
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute(AuthConstant.AUTH_SESSION_REDIS);
        if (attribute == null && demoEnabled) {
            MemberVO visitor = new MemberVO();
            visitor.setId(ThreadLocalRandom.current().nextLong(1_000_000_000L, 8_000_000_000_000L));
            visitor.setUsername("demo-visitor");
            visitor.setNickname("演示访客");
            visitor.setIntegration(0);
            session.setAttribute(AuthConstant.AUTH_SESSION_REDIS, visitor);
            session.setMaxInactiveInterval(1800);
            attribute = visitor;
        }
        if(attribute != null){
            MemberVO memberVO = (MemberVO) attribute;
            threadLocal.set(memberVO);
            return true;
        }
        // 如果 attribute == null 说明没有登录，那么我们就需要重定向到登录页面
        session.setAttribute(AuthConstant.AUTH_SESSION_MSG,"请先登录");
        response.sendRedirect("http://auth.msb.com/login.html");
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) {
        threadLocal.remove();
    }
}
