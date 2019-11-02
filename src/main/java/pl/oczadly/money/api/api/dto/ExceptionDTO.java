package pl.oczadly.money.api.api.dto;

public class ExceptionDTO {

    private final String message;

    public ExceptionDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
