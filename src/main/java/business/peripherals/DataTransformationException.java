package business.peripherals;

public class DataTransformationException extends RuntimeException {
    public DataTransformationException(String message) {
        super("\nData Transformation Exception: " + message.trim());
    }
}
