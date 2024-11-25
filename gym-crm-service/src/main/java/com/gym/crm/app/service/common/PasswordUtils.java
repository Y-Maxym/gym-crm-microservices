package com.gym.crm.app.service.common;

import com.gym.crm.app.exception.PasswordOperationException;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

import static com.gym.crm.app.rest.exception.ErrorCode.HASHED_ERROR;

@Component
public class PasswordUtils implements PasswordEncoder {

    private static final char[] LOWERCASE_LETTERS = {'a', 'z'};
    private static final char[] UPPER_LETTERS = {'A', 'Z'};
    private static final char[] DIGITS = {'0', '9'};

    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH = 256;

    private static final String PASSWORD_PATTERN = "%s:%s";
    private static final String SPLIT_REGEX = ":";
    private static final String HASHED_EXCEPTION = "Password cannot be hashed";

    @Value("${security.password.length}")
    private int passwordLength;

    public String generatePassword() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange(LOWERCASE_LETTERS, UPPER_LETTERS, DIGITS)
                .get();

        return generator.generate(passwordLength);
    }

    public String hashPassword(String password) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);

        return PASSWORD_PATTERN.formatted(salt, hashedPassword);
    }

    public boolean isPasswordCorrect(String inputPassword, String storedPassword) {
        String[] parts = storedPassword.split(SPLIT_REGEX);
        String salt = parts[0];
        String storedHash = parts[1];

        String inputHash = hashPassword(inputPassword, salt);

        return inputHash.equals(storedHash);
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();

        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);

        return Base64.getEncoder().encodeToString(saltBytes);
    }

    private String hashPassword(String password, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(HASH_ALGORITHM);

            byte[] hashedPasswordBytes = factory.generateSecret(spec).getEncoded();

            return Base64.getEncoder().encodeToString(hashedPasswordBytes);
        } catch (Exception e) {
            throw new PasswordOperationException(HASHED_EXCEPTION, HASHED_ERROR.getCode(), e);
        }
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return hashPassword(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return isPasswordCorrect(rawPassword.toString(), encodedPassword);
    }
}
