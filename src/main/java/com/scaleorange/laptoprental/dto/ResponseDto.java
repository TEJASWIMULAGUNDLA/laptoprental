package com.scaleorange.laptoprental.dto;


import lombok.Data;

@Data
public class ResponseDto {
    private String token;
    private String isLogged;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIsLogged() {
        return isLogged;
    }

    public void setIsLogged(String isLogged) {
        this.isLogged = isLogged;
    }
}
