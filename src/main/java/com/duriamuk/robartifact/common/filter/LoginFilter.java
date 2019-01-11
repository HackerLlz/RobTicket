package com.duriamuk.robartifact.common.filter;

import com.duriamuk.robartifact.common.constant.AjaxMessage;
import com.duriamuk.robartifact.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.thymeleaf.util.ArrayUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: DuriaMuk
 * @description:
 * @WebFilter将一个实现了javax.servlet.Filter接口的类定义为过滤器 <br/>
 * 属性filterName声明过滤器的名称,可选属性urlPatterns指定要过滤的URL模式,也可使用属性value来声明.<br/>
 * 指定要过滤的URL模式是必选属性<br/>
 * @create: 2019-01-10 15:57
 */
@Component
@WebFilter(filterName = "loginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private LoginService loginService;

    // 自定义白名单
    public final static List<String> IS_NOT_LOGIN_VALIDATE_PATH = new ArrayList<String>();

    // 静态资源后缀名
    public final static String[] STATIC_RESCUE = new String[]{
            ".jpg", ".jpeg", ".gif", ".css", ".js", ".png", ".bmp", ".ico",
            ".txt", ".mp3", ".eot", ".svg", ".ttf", ".woff", ".woff2", ".map"
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        logger.info("开始登陆过滤器");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();
        logger.info("uri入参：{}", uri);
        if (isStaticRescue(uri) || isNotLoginValidate(uri)) {
            logger.info("白名单：{}", uri);
            chain.doFilter(request, response);
            return;
        }
        request.setAttribute("requestId", Long.toString(System.currentTimeMillis()));

        boolean isLogin = loginService.isLogin();
        if (isLogin) {
            // 已登录
            chain.doFilter(request, response);
            return;
        } else {
            // 未登录
            if (isAjaxRequest(request)) {
                //如果是ajax 请求
//            response.setContentType("application/json;charset=utf-8");
                response.setContentType("text/html;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.write(AjaxMessage.NOLOGIN);
                out.flush();
                out.close();
            } else {
                // 返回一个未登录页面 js-跳转到登录页
                response.setContentType("text/html");
                response.getWriter().print("<script>document.location.href='/login/view'</script>");
                return;
            }
        }
    }

    private boolean isStaticRescue(String url) {
        if (url.lastIndexOf(".") == -1) {
            return false;
        }
        String suffix = url.substring(url.lastIndexOf("."));
        return ArrayUtils.contains(STATIC_RESCUE, suffix);
    }

    private boolean isNotLoginValidate(String uri) {
        // uri特征过滤
        if (uri.equals("/")
                || uri.startsWith("/login")
                || uri.startsWith("/passenger")
                || uri.startsWith("/ticket")
                || uri.endsWith("/logout")
                || uri.endsWith("/view")

                || uri.startsWith("/js")
        ) {
            return true;
        }
        // 白名单过滤
        for (String path : IS_NOT_LOGIN_VALIDATE_PATH) {
            if (pathMatcher.match(path, uri)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return (request.getHeader("X-Requested-With") != null &&
                "XMLHttpRequest".equals(request.getHeader("X-Requested-With")));
    }
}
