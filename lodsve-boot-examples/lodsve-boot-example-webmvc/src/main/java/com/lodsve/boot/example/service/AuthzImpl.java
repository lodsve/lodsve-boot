package com.lodsve.boot.example.service;

import com.lodsve.boot.component.security.core.Account;
import com.lodsve.boot.component.security.exception.AuthException;
import com.lodsve.boot.component.security.service.Authorization;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 */
@Component
public class AuthzImpl implements Authorization {
    public static boolean LOGIN = false;

    @Override
    public boolean isLogin(HttpServletRequest request) {
        return LOGIN;
    }

    @Override
    public void ifNotLogin(HttpServletRequest request, HttpServletResponse response) {
        throw new AuthException(105001, "no login!");
    }

    @Override
    public boolean authz(Account account, String... roles) {
        return "admin".equals(roles[0]);
    }

    @Override
    public void ifNotAuth(HttpServletRequest request, HttpServletResponse response, Account account) {
        throw new AuthException(105001, "no permission!");
    }

    @Override
    public Account getCurrentUser(HttpServletRequest request) {
        return new Account(1L, "sunhao");
    }
}
