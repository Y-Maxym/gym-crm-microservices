package com.gym.crm.app.security;

import com.gym.crm.app.entity.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticatedUserUtil {

    public static User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    public static void addRefreshTokenToCookies(String refreshToken, HttpHeaders headers) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(-1)
                .build();

        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void deleteCookie(String cookieName, HttpHeaders headers) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .maxAge(0)
                .build();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void deleteHeader(String header, HttpHeaders headers) {
        headers.set(header, "");
    }
}
