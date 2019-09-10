package com.zanclick.prepay.auth.filter.impl;

import com.zanclick.prepay.auth.entity.UsernamePasswordToken;
import com.zanclick.prepay.auth.filter.AbstractJwtAuthenticationFilter;
import com.zanclick.prepay.auth.secure.resolver.TokenStoreResolver;
import com.zanclick.prepay.auth.secure.resolver.UserPermissionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * @author tuchuan
 * @description
 * @date 2019/3/15 10:50
 */
//@WebFilter(urlPatterns = "/*")
public class AuthFilterAbstract extends AbstractJwtAuthenticationFilter {

    @Autowired
    @Qualifier(value = "webUserPermissionResolver")
    private UserPermissionResolver userPermissionResolver;

    @Autowired
    @Qualifier(value = "webTokenResolver")
    private TokenStoreResolver tokenStoreResolver;

    @Override
    public UserPermissionResolver getUserPermissionResolver() {
        return userPermissionResolver;
    }

    @Override
    public TokenStoreResolver getTokenStoreResolver() {
        return tokenStoreResolver;
    }

    @Override
    protected UsernamePasswordToken createUsernamePasswordToken(HttpServletRequest request) {
        return super.createUsernamePasswordToken(request);
    }

    @Override
    protected Boolean isLoginUrl(HttpServletRequest request) {
        return pathMatcher.match("/login", request.getServletPath());
    }

    @Override
    protected Boolean isLogoutUrl(HttpServletRequest request) {
        return pathMatcher.match("/logout", request.getServletPath());
    }

    @Override
    protected boolean isProtectedUrl(HttpServletRequest request) {
        return pathMatcher.match("/api/**", request.getServletPath())
                && !pathMatcher.match("/api/open/**", request.getServletPath());
    }
}
