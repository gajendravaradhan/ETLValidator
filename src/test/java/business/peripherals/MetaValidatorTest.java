package business.peripherals;

import business.peripherals.exceptions.CustomValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

public class MetaValidatorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private MetaValidator target;

    @BeforeTest
    public void setUp() {
        target = new MetaValidator();
    }

    @AfterTest
    public void tearDown() {
    }

    @Test
    public void testIsEmptyFile() throws IOException, CustomValidationException {
        assertTrue(MetaValidator.isEmptyFile("src/test/resources/unit-test-data/empty.json"));
    }

    @Test
    public void testValidateFile() throws IOException {
        assertTrue(MetaValidator.validateFile("src/test/resources/unit-test-data/expected.json", "json"));
    }

    @Test
    public void testValidateMimeType() throws CustomValidationException, IOException {
        assertTrue(MetaValidator.validateMimeType("src/test/resources/unit-test-data/expected.json", "json"));
    }

    @Test
    public void testValidateMimeTypeEmptyFile() {
        Assert.assertThrows(CustomValidationException.class, () -> MetaValidator.validateMimeType("src/test/resources/unit-test-data/empty.json", "json"));
        Assert.assertThrows(CustomValidationException.class, () -> MetaValidator.validateMimeType("src/test/resources/unit-test-data/Non-existant.json", "json"));
    }

    @Test
    public void testValidateMimeTypeSpuriousFile() {
        Assert.assertThrows(CustomValidationException.class, () -> MetaValidator.validateMimeType("src/test/resources/unit-test-data/spuriousDataTest.json", "csv"));
    }

    @Test
    public void testValidateNonExistentFile() throws IOException {
        assertFalse(MetaValidator.validateFile("src/test/resources/unit/expected.json", "json"));
        assertFalse(MetaValidator.validateFile("src/test/resources/unit-test-data/spuriousDataTest.json", "csv"));
    }

    @Test
    public void testValidatePath() throws CustomValidationException {
        assertTrue(MetaValidator.validatePath("src/test/resources/unit-test-data/expected.json"));
    }

    @Test
    public void testValidatePathWrongPath() {
        assertThrows(CustomValidationException.class, () -> MetaValidator.validatePath("src/test/resources/unit/expected.json"));
    }
}