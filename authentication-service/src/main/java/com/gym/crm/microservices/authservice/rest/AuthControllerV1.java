package com.gym.crm.microservices.authservice.rest;

import com.gym.crm.microservices.authservice.model.UserCredentials;
import com.gym.crm.microservices.authservice.service.AuthService;
import com.gym.crm.microservices.authservice.service.AuthenticatedUserUtil;
import com.gym.crm.microservices.authservice.service.JwtService;
import com.gym.crm.microservices.authservice.service.RefreshTokenService;
import com.gym.crm.microservices.authservice.validator.AccessTokenValidator;
import com.gym.crm.microservices.authservice.validator.RefreshTokenValidator;
import com.gym.crm.microservices.authservice.validator.UserCredentialsValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("${api.base-path}")
@RequiredArgsConstructor
public class AuthControllerV1 {

    private final AuthService service;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserCredentialsValidator userCredentialsValidator;
    private final RefreshTokenValidator refreshTokenValidator;
    private final AccessTokenValidator accessTokenValidator;

    @InitBinder("userCredentials")
    public void initUserCredentialsBinder(WebDataBinder binder) {
        binder.addValidators(userCredentialsValidator);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated UserCredentials credentials,
                                   BindingResult bindingResult) {
        service.authenticate(credentials, bindingResult);

        String username = credentials.getUsername();
        HttpHeaders headers = new HttpHeaders();
        accessToken(username, headers);
        refreshToken(username, headers);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).build();
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        invalidateAccessToken(authorization);
        invalidateRefreshToken(request);

        HttpHeaders headers = new HttpHeaders();
        AuthenticatedUserUtil.deleteHeader("Authorization", headers);
        AuthenticatedUserUtil.deleteCookie("refreshToken", headers);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@CookieValue(name = "refreshToken", required = false)
                                                String token,
                                                HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        refreshTokenValidator.validate(token);
        accessTokenValidator.validate(authorization);

        invalidateAccessToken(authorization);

        String username = refreshTokenService.findUsernameByToken(token);
        HttpHeaders headers = new HttpHeaders();
        accessToken(username, headers);
        refreshToken(username, headers);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).build();
    }

    private void accessToken(String username, HttpHeaders headers) {
        String token = jwtService.generateToken(username);
        headers.set("Authorization", "Bearer " + token);
    }

    private void refreshToken(String username, HttpHeaders headers) {
        String token = refreshTokenService.generateToken(username);
        AuthenticatedUserUtil.addRefreshTokenToCookies(token, headers);
    }

    private void invalidateAccessToken(String authorization) {
        String token = jwtService.extractAccessToken(authorization);
        jwtService.addTokenToBlackList(token);
    }

    private void invalidateRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (isNull(cookies) || cookies.length == 0) {
            return;
        }

        String token = Arrays.stream(cookies)
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        refreshTokenService.deleteByToken(token);
    }
}
