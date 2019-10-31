package controller;

import spark.Request;

public class ControllerUtils {

    private ControllerUtils() {
    }

    public static Long parseIdFromNamedQueryParams(Request request) {
        String idQueryParam = request.params(":id");
        return Long.parseLong(idQueryParam);
    }
}
