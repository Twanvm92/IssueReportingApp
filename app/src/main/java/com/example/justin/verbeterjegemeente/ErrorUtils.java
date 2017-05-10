package com.example.justin.verbeterjegemeente;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by twanv on 10-5-2017.
 */

public class ErrorUtils {

    public static APIError parseError(Response<?> response) {
        Converter<ResponseBody, APIError> converter =
                ServiceGenerator.retrofit
                        .responseBodyConverter(APIError.class, new Annotation[0]);

        APIError error;


        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new APIError();
//        return new APIError(12,"ja","ja");
    }
}
