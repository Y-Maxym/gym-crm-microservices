package com.gym.crm.app.service.common;

import com.gym.crm.app.exception.PasswordOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKeyFactory;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class PasswordUtilsTest {

    @InjectMocks
    private PasswordUtils utils;

    @RepeatedTest(10)
    @DisplayName("Test generate password with valid length and contains allowed characters")
    public void whenGeneratePassword_thenPasswordIsGenerated() {
        // given
        ReflectionTestUtils.setField(utils, "passwordLength", 100);

        // when
        String password = utils.generatePassword();

        // then
        assertThat(password).matches("[a-zA-Z0-9]+");
    }

    @Test
    @DisplayName("Test hash password with valid length")
    public void givenLength_whenGeneratePassword_thenReturnsPasswordWithCorrectSaltAndHashLength() {
        // given
        String password = "password";

        // when
        String hashedPassword = utils.hashPassword(password);

        String[] parts = hashedPassword.split(":");
        String salt = parts[0];
        String hash = parts[1];

        // then
        assertThat(parts).hasSize(2);
        assertThat(salt).hasSize(24);
        assertThat(hash).isNotEmpty();
    }

    @Test
    @DisplayName("Test password verification with correct password")
    public void givenStoredPassword_whenIsPasswordCorrect_thenReturnsTrueForCorrectPassword() {
        // given
        String plainPassword = "mySecurePassword";

        String salt = ReflectionTestUtils.invokeMethod(utils, "generateSalt");
        String hashedPassword = ReflectionTestUtils.invokeMethod(utils, "hashPassword", plainPassword, salt);

        String storedPassword = "%s:%s".formatted(salt, hashedPassword);

        // when
        boolean result = utils.isPasswordCorrect(plainPassword, storedPassword);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Test hashPassword method handles exceptions correctly")
    public void givenValidInputs_whenHashPassword_thenDoesNotThrowException() throws NoSuchAlgorithmException {
        // given
        String plainPassword = "password";
        String salt = "salt";

        MockedStatic<SecretKeyFactory> mockStatic = mockStatic(SecretKeyFactory.class);

        given(SecretKeyFactory.getInstance(anyString()))
                .willThrow(new NoSuchAlgorithmException());

        // when
        PasswordOperationException ex = assertThrows(PasswordOperationException.class, () -> {
            ReflectionTestUtils.invokeMethod(utils, "hashPassword", plainPassword, salt);
        });
        Throwable cause = ex.getCause();

        // then
        assertThat(ex.getMessage()).isEqualTo("Password cannot be hashed");
        assertThat(cause).isInstanceOf(NoSuchAlgorithmException.class);

        mockStatic.close();
    }


}