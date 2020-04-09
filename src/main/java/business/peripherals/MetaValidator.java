package business.peripherals;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * The type Meta validator.
 */
public class MetaValidator {
    private static Logger LOGGER = LoggerFactory.getLogger(MetaValidator.class);

    /**
     * Validate path boolean.
     *
     * @param path the path
     * @return the boolean
     */
    public static Boolean validatePath(String path) throws CustomValidationException {
        File file = new File(path);
        if(!file.exists()) {
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
    public static Boolean validateMimeType(String path, String expected) throws IOException, CustomValidationException {
        if(isEmptyFile(path)) {
            throw new CustomValidationException("\n\n File is empty");
        }
        File file = new File(path);
        Tika tika = new Tika();
        String fileType = tika.detect(file);
        if(!fileType.contains(expected)) {
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
    public static Boolean isEmptyFile (String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        return br.readLine() == null;
    }

    /**
     * Validate file boolean.
     *
     * @param path     the path
     * @param expected the expected
     * @return the boolean
     * @throws IOException               the io exception
     * @throws CustomValidationException the custom validation exception
     */
    public static void validateFile(String path, String expected) throws IOException, CustomValidationException {
        if(validatePath(path)) {
            validateMimeType(path, expected);
        }
    }
}
