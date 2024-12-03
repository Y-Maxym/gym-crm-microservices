package com.gym.crm.microservices.authservice.service.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public class AuthenticatedUserUtil {

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
