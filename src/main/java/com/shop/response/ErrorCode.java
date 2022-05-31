package com.shop.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_EMAIL(BAD_REQUEST, "검색할 이메일을 전달해주세요"),
    INVALID_TOKEN(BAD_REQUEST, "토큰이 유효하지 않습니다"),
    INVALID_SIGNUP_EMAIL(BAD_REQUEST, "가입 할수 없는 E-mail 입니다."),
    INVALID_EXPIRED_TOKEN(BAD_REQUEST, "토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "refresh 토큰이 유효하지 않습니다"),
    INVALID_EXPIRED_REFRESH_TOKEN(BAD_REQUEST, "refresh 토큰이 만료되었습니다."),
    INVALID_EMAIL_PASSWORD(BAD_REQUEST, "로그인할 Email과 비밀번호를 전달해주세요"),
    INVALID_FORBIDDEN(FORBIDDEN, "토큰이 만료 되었습니다."),
    INVALID_UNAUTHORIZED(UNAUTHORIZED, "인증 오류 입니다.");

    private final HttpStatus httpStatus;
    private final String detail;
}