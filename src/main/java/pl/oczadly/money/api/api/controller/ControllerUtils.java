package pl.oczadly.money.api.api.controller;

import pl.oczadly.money.api.api.dto.ExceptionDTO;
import spark.Request;

public class ControllerUtils {

    private ControllerUtils() {
    }

    public static Long parseIdFromNamedQueryParams(Request request) {
        String idQueryParam = request.params(":id");
        return Long.parseLong(idQueryParam);
    }

    public static ExceptionDTO mapToExceptionDTO(Exception exception) {
        String message = exception.getMessage();
        return new ExceptionDTO(message);
    }

}
