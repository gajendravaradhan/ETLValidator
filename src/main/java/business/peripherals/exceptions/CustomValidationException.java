package business.peripherals.exceptions;

public class CustomValidationException extends Exception {

    public CustomValidationException(String message) {
        super("\nMeta Data Validation Exception: " + message.trim());
    }
}
