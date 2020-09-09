package infrastructure.auth;

import javax0.license3j.License;
import javax0.license3j.io.IOFormat;
import javax0.license3j.io.LicenseReader;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class Authenticator {


    public static final String validateKey() {
        License license = null;
        try (LicenseReader reader = new LicenseReader("src/main/resources/license.bin")) {
            license = reader.read(IOFormat.BASE64);
        } catch (IOException e) {
            System.out.println("Error reading license file " + e);
        }

        final byte[] key = new byte[]{
                (byte) 0x52,
                (byte) 0x53, (byte) 0x41, (byte) 0x00, (byte) 0x30, (byte) 0x81, (byte) 0x9F, (byte) 0x30, (byte) 0x0D,
                (byte) 0x06, (byte) 0x09, (byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xF7, (byte) 0x0D,
                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0x81, (byte) 0x8D,
                (byte) 0x00, (byte) 0x30, (byte) 0x81, (byte) 0x89, (byte) 0x02, (byte) 0x81, (byte) 0x81, (byte) 0x00,
                (byte) 0x93, (byte) 0x70, (byte) 0x7F, (byte) 0x9E, (byte) 0x7E, (byte) 0x7D, (byte) 0xD7, (byte) 0x38,
                (byte) 0x86, (byte) 0x5A, (byte) 0x17, (byte) 0xA3, (byte) 0xF6, (byte) 0xBF, (byte) 0x9A, (byte) 0x43,
                (byte) 0x96, (byte) 0x71, (byte) 0x9F, (byte) 0x41, (byte) 0xDD, (byte) 0x7E, (byte) 0x61, (byte) 0x9D,
                (byte) 0xEC, (byte) 0xD2, (byte) 0x8A, (byte) 0x3C, (byte) 0xFF, (byte) 0x9D, (byte) 0x86, (byte) 0x7F,
                (byte) 0x75, (byte) 0x0F, (byte) 0x66, (byte) 0x27, (byte) 0x78, (byte) 0xC7, (byte) 0xDE, (byte) 0xD7,
                (byte) 0x1B, (byte) 0xB1, (byte) 0x4E, (byte) 0x65, (byte) 0x2E, (byte) 0x09, (byte) 0x83, (byte) 0x86,
                (byte) 0xA2, (byte) 0x58, (byte) 0x89, (byte) 0x31, (byte) 0x2C, (byte) 0x27, (byte) 0xC7, (byte) 0xED,
                (byte) 0x41, (byte) 0x0A, (byte) 0x08, (byte) 0x8A, (byte) 0x5D, (byte) 0x84, (byte) 0x40, (byte) 0xAC,
                (byte) 0x23, (byte) 0x5C, (byte) 0xD5, (byte) 0xF0, (byte) 0x32, (byte) 0x93, (byte) 0x7F, (byte) 0x4E,
                (byte) 0x3C, (byte) 0x7E, (byte) 0x00, (byte) 0x13, (byte) 0x2F, (byte) 0xAE, (byte) 0x41, (byte) 0xA4,
                (byte) 0xB2, (byte) 0x4E, (byte) 0xEF, (byte) 0x50, (byte) 0xFA, (byte) 0xD3, (byte) 0x26, (byte) 0xC7,
                (byte) 0x36, (byte) 0xB0, (byte) 0x22, (byte) 0x2A, (byte) 0x8E, (byte) 0x2B, (byte) 0x30, (byte) 0x07,
                (byte) 0xE5, (byte) 0xD0, (byte) 0x54, (byte) 0x55, (byte) 0x2C, (byte) 0xAF, (byte) 0xEF, (byte) 0xF5,
                (byte) 0xA9, (byte) 0xD9, (byte) 0x01, (byte) 0x0B, (byte) 0xCB, (byte) 0x31, (byte) 0x3C, (byte) 0xE2,
                (byte) 0x08, (byte) 0x67, (byte) 0xD3, (byte) 0x45, (byte) 0x5F, (byte) 0x31, (byte) 0x97, (byte) 0x18,
                (byte) 0x64, (byte) 0x10, (byte) 0x90, (byte) 0x27, (byte) 0xAF, (byte) 0x99, (byte) 0x16, (byte) 0xFD,
                (byte) 0x02, (byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x01,
        };

        if (!license.isOK(key)) {
            // if not signed, stop the application
            return "Not a valid license";
        }
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        final LocalDate licenseDate = LocalDate.parse(license.getFeatures().get("DATED").valueString(), formatter);

        try {
            if (Period.between(licenseDate, currentDate()).getMonths() < 0) {
                return "License Expired";
            }
        } catch (IOException e) {
            return "Unable to connect to \"time-a.nist.gov\". Please check your internet connectivity";
        }
        return "Valid license";
    }

    private static final LocalDate currentDate() throws IOException {
        String TIME_SERVER = "time-a.nist.gov";
        NTPUDPClient timeClient = new NTPUDPClient();
        InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
        TimeInfo timeInfo = timeClient.getTime(inetAddress);
        long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
        Date time = new Date(returnTime);
        return convertToLocalDateViaSqlDate(time);
    }

    private static final LocalDate convertToLocalDateViaSqlDate(Date dateToConvert) {
        return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
    }

}
