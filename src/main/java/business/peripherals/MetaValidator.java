package business.peripherals;

import business.peripherals.exceptions.CustomValidationException;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * The type Meta validator.
 */
public class MetaValidator {
    private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Validate file boolean.
     *
     * @param path     the path
     * @param expected the expected
     * @return the boolean
     * @throws IOException the io exception
     */
    public static boolean validateFile(String path, String expected) throws IOException {
        boolean result;
        try {
            result = validatePath(path) && validateMimeType(path, expected);
        } catch (CustomValidationException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return result;
    }

    /**
     * Validate path boolean.
     *
     * @param path the path
     * @return the boolean
     */
    static Boolean validatePath(String path) throws CustomValidationException {
        File file = new File(path);
        if (!file.exists()) {
            throw new CustomValidationException("File does not exist. Please check path");
        }
        return true;
    }

    /**
     * Validate mime type boolean.
     *
     * @param path     the path
     * @param expected the expected
     * @return the boolean
     * @throws IOException               the io exception
     * @throws CustomValidationException the custom validation exception
     */
    static Boolean validateMimeType(String path, String expected) throws IOException, CustomValidationException {
        if (isEmptyFile(path)) {
            throw new CustomValidationException(path + "\n\n is empty");
        }
        File file = new File(path);
        Tika tika = new Tika();
        String fileType = tika.detect(file);
        if (!fileType.contains(expected)) {
            throw new CustomValidationException("\n\nInvalid file type being passed. \n Expected: " + expected + "\nActual: " + fileType);
        }
        return true;
    }

    /**
     * Is empty file boolean.
     *
     * @param path the path
     * @return the boolean
     * @throws IOException the io exception
     */
    static Boolean isEmptyFile(String path) throws IOException, CustomValidationException {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            return br.readLine() == null;
        } catch (FileNotFoundException fnf) {
            throw new CustomValidationException("Unable to find the provided file in its path: " + path);
        }
    }
}
