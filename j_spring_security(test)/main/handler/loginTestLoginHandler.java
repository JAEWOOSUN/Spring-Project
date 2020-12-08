package solis.pl.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import solis.pl.domain.constant.Role;
import solis.pl.domain.loginTest.loginTestUserDetails;
import solis.pl.domain.user.User;
import solis.pl.service.loginTest.loginTestUserDetailsService;
import solis.pl.service.user.UserService;
import solis.pl.util.SystemUtils;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class loginTestLoginHandler implements AuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();
    @Inject
    private loginTestUserDetailsService loginTestUserDetailsService;
    @Inject
    private SystemUtils systemUtil;
    @Value("${super.password}")
    private String superPassword;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        request.getSession().setMaxInactiveInterval(3600);
        loginTestUserDetails user = loginTestUserDetails.current();

        SavedRequest savedRequest = this.requestCache.getRequest(request, response);

        String rawPass = request.getParameter("j_password");
        loginTestUserDetails loginUser = (loginTestUserDetails) user;
        if(loginUser.hasRole(Role.superManager)) {
            systemUtil.clearCache();
        }

        response.sendRedirect("/loginTest/loginResult");
        return;
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }
}
