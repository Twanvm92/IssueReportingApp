package com.example.justin.verbeterjegemeente;

/**
 * Created by twanv on 10-5-2017.
 */

public class APIError {

    private int statusCode;
    private String code;
    private String description;

    public APIError() {

    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
