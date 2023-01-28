package chatApp.filter;

import chatApp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static chatApp.utilities.Utility.*;

@Component
public class AuthFilter extends GenericFilterBean {

    @Autowired
    AuthService authService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        res.setHeader("Access-Control-Allow-Origin", "http://localhost:9000");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Allow-Methods", "ACL, CANCELUPLOAD, CHECKIN, CHECKOUT, PATCH, COPY, DELETE, GET, HEAD, LOCK, MKCALENDAR, MKCOL, MOVE, OPTIONS, POST, PROPFIND, PROPPATCH, PUT, REPORT, SEARCH, UNCHECKOUT, UNLOCK, UPDATE, VERSION-CONTROL");
        res.setHeader("Access-Control-Max-Age", "86400");
        res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Key, Authorization");
        String auth = req.getParameter("token");
        String path = req.getRequestURI();
        if (permissionPathsForAll.stream().noneMatch(path::contains)) {
            if (!authService.getKeyTokensValEmails().containsKey(auth)) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            } else {
                String userEmail = authService.getKeyTokensValEmails().get(auth);
                if (!auth.equals(authService.getKeyEmailsValTokens().get(userEmail))) {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return ;
                }
            }
        }

        chain.doFilter(request, response);

    }
}