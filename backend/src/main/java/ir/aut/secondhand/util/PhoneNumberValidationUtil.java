package ir.aut.secondhand.util;

public class PhoneNumberValidationUtil {

    public static String normalizePhoneNumber(String phoneNumber) {

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }

        phoneNumber = phoneNumber.trim();

        if (phoneNumber.startsWith("09")) {
            phoneNumber = "+98" + phoneNumber.substring(1);
        } else if (phoneNumber.startsWith("9")) {
            phoneNumber = "+98" + phoneNumber;
        }

        if (!phoneNumber.matches("^\\+989\\d{9}$")) {
            throw new IllegalArgumentException("Invalid Iranian phone number.");
        }

        return phoneNumber;
    }
}
