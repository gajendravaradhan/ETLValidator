package business.peripherals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomValidationException extends Exception {

    public CustomValidationException(String message) {
        super("\nMeta Data Validation Exception: " + message.trim());
    }
}
