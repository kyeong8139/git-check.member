package com.git_check.member.auth.adapter.config.exception;

import org.springframework.http.HttpStatus;

import com.git_check.common_module.response.exception.ServiceException;

public class InvalidOAuth2ProviderException extends ServiceException{
    
    public InvalidOAuth2ProviderException() {
        super(HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인 방식입니다.");
    }
}
