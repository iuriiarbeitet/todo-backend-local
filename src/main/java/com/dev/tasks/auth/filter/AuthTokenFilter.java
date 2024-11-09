package com.dev.tasks.auth.filter;

import com.dev.tasks.auth.entity.User;
import com.dev.tasks.auth.exception.JwtCommonException;
import com.dev.tasks.auth.service.UserDetailsImpl;
import com.dev.tasks.auth.utils.CookieUtils;
import com.dev.tasks.auth.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";

    private JwtUtils jwtUtils;
    private CookieUtils cookieUtils;

    private final List<String> permitURL = Arrays.asList(
            "register",
            "login",
            "activate-account",
            "resend-activate-email",
            "send-reset-password-email",
            "test-no-auth",
            "index"

    );


    @Autowired
    public void setJwtUtils(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Autowired
    public void setCookieUtils(CookieUtils cookieUtils) {
        this.cookieUtils = cookieUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        boolean isRequestToPublicAPI = permitURL.stream().anyMatch(s -> request.getRequestURI().toLowerCase().contains(s));

        if (
                !isRequestToPublicAPI && !request.getMethod().equals(HttpMethod.OPTIONS.toString())
        ) {

            String jwt = null;

            if (request.getRequestURI().contains("update-password")) {
                jwt = getJwtFromHeader(request);
            } else {
                jwt = cookieUtils.getCookieAccessToken(request);
            }

            if (jwt != null) {

                if (jwtUtils.validate(jwt)) {

                    User user = jwtUtils.getUser(jwt);

                    UserDetailsImpl userDetails = new UserDetailsImpl(user);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } else {
                    throw new JwtCommonException("jwt validate exception");
                }
            } else {
                throw new AuthenticationCredentialsNotFoundException("token not found");
            }

        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromHeader(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(BEARER_PREFIX)) {
            return headerAuth.substring(7);
        }
        return null;
    }

}
