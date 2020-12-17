package solis.pl.handler;


import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class loginTestLoginFailureHandler implements AuthenticationFailureHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException exception) throws IOException, ServletException {
        String targetURL = determinTargetURL(request);

        System.out.println("failure");

        if (response.isCommitted()) {
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetURL);
    }

    public String determinTargetURL(HttpServletRequest request) {
        System.out.println("determinTargetUrl pass");
        return "/loginTest/signin" + "?signinError=true";
    }
}
