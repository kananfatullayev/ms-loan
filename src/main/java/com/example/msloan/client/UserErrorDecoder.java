package com.example.msloan.client;

import com.example.msloan.model.exception.ExceptionDto;
import com.example.msloan.model.exception.NotFoundException;
import com.example.msloan.model.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;

public class UserErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        ExceptionDto message;

        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ExceptionDto.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }

        return switch (response.status()) {
            case 404 -> new NotFoundException(message.getCode() != null ? message.getCode() : "NOT_FOUND");
            case 403 -> new ValidationException(message.getCode() != null ? message.getCode() : "VALIDATION_EXCEPTION");
            default -> new RuntimeException(message.getCode() != null ? message.getCode() : "CLIENT_EXCEPTION");
        };
    }
}
