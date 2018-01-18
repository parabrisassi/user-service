package com.parabrisassi.sist.user_service.security.authentication;

import com.parabrisassi.sist.user_service.error_handling.errros.ValidationError;
import com.parabrisassi.sist.user_service.exceptions.ValidationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import static com.parabrisassi.sist.user_service.security.authentication.PasswordValidatorImpl.MAX_PASSWORD_LENGTH;
import static com.parabrisassi.sist.user_service.security.authentication.PasswordValidatorImpl.MIN_PASSWORD_LENGTH;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class PasswordValidationImplTest {

    private final static String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private final static String NUMBERS = "0123456789";
    private final static String SPECIAL_CHARACTERS = "~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";


//    private final static String VALID_PASSWORD = ".MoThër-FÚcker123_%@$";

    @Autowired
    private PasswordValidatorImpl passwordValidator;

    @Test
    public void testValidPassword() {
        passwordValidator.validate(generateValidPassword());
    }

    @Test(expected = ValidationException.class)
    public void testNullPasswordThrowsException() {
        passwordValidator.validate(generateNullPassword());
    }

    @Test
    public void testNullPasswordReportsError() {
        try {
            testNullPasswordThrowsException();
        } catch (ValidationException e) {
            assertErrorList(e, PasswordValidatorImpl.MISSING_PASSWORD);
        }
    }

    @Test(expected = ValidationException.class)
    public void testShortPasswordThrowsException() {
        passwordValidator.validate(generateShortPassword());
    }

    @Test
    public void testShortPasswordReportsError() {
        try {
            testShortPasswordThrowsException();
        } catch (ValidationException e) {
            assertErrorList(e, PasswordValidatorImpl.PASSWORD_TOO_SHORT);
        }
    }

    @Test(expected = ValidationException.class)
    public void testLongPasswordThrowsException() {
        passwordValidator.validate(generateLongPassword());
    }

    @Test
    public void testLongPasswordReportsError() {
        try {
            testLongPasswordThrowsException();
        } catch (ValidationException e) {
            assertErrorList(e, PasswordValidatorImpl.PASSWORD_TOO_LONG);
        }
    }

    @Test(expected = ValidationException.class)
    public void testMissingUpperCaseLetterThrowsException() {
        passwordValidator.validate(generateMissingUppercasePassword());
    }

    @Test
    public void testMissingUpperCaseLetterReportsError() {
        try {
            testMissingUpperCaseLetterThrowsException();
        } catch (ValidationException e) {
            assertErrorList(e, PasswordValidatorImpl.PASSWORD_UPPER_CASE_MISSING);
        }
    }

    @Test(expected = ValidationException.class)
    public void testMissingLowerCaseLetterThrowsException() {
        passwordValidator.validate(generateMissingLowercasePassword());
    }

    @Test
    public void testMissingLowerCaseLetterReportsError() {
        try {
            testMissingLowerCaseLetterThrowsException();
        } catch (ValidationException e) {
            assertErrorList(e, PasswordValidatorImpl.PASSWORD_LOWER_CASE_MISSING);
        }
    }

    @Test(expected = ValidationException.class)
    public void testMissingNumberThrowsException() {
        passwordValidator.validate(generateMissingNumberPassword());
    }

    @Test
    public void testMissingNumberReportsError() {
        try {
            testMissingNumberThrowsException();
        } catch (ValidationException e) {
            assertErrorList(e, PasswordValidatorImpl.PASSWORD_NUMBER_MISSING);
        }
    }

    @Test(expected = ValidationException.class)
    public void testMissingSpecialCharacterThrowsException() {
        passwordValidator.validate(generateMissingSpecialCharacterPassword());
    }

    @Test
    public void testMissingSpecialCharacterReportsError() {
        try {
            testMissingSpecialCharacterThrowsException();
        } catch (ValidationException e) {
            assertErrorList(e, PasswordValidatorImpl.PASSWORD_SPECIAL_CHARACTER_MISSING);
        }
    }


    /**
     * Asserts that the given {@link ValidationException} contains a valid {@link ValidationError} list,
     * containing the {@code shouldContainError}.
     *
     * @param e                  The {@link ValidationException} that must be validated.
     * @param shouldContainError The {@link ValidationError} the list must contain.
     */
    private static void assertErrorList(ValidationException e, ValidationError shouldContainError) {
        final List<ValidationError> errors = e.getErrors();
        Assert.assertNotNull(errors);
        Assert.assertTrue(errors.contains(shouldContainError));
    }

    private static String generateValidPassword() {
        final char[] possibleCharacters = (UPPERCASE_LETTERS + LOWERCASE_LETTERS + NUMBERS + SPECIAL_CHARACTERS)
                .toCharArray();
        final int length = RandomUtils.nextInt(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH);
        return RandomStringUtils.random(length, 0, possibleCharacters.length - 1,
                false, false, possibleCharacters, new SecureRandom());
    }

    /**
     * Generates a {@code null} password.
     *
     * @return {@code null}.
     */
    private static String generateNullPassword() {
        return null;
    }

    /**
     * Generates a short password.
     *
     * @return A password with a short length, according to {@link PasswordValidatorImpl#MIN_PASSWORD_LENGTH}.
     */
    private static String generateShortPassword() {
        final int length = MIN_PASSWORD_LENGTH <= 0 ?
                0 : new Random().nextInt(MIN_PASSWORD_LENGTH);
        return RandomStringUtils.random(length);
    }

    /**
     * Generates a long password.
     *
     * @return A password with a short length, according to {@link PasswordValidatorImpl#MAX_PASSWORD_LENGTH}.
     */
    private static String generateLongPassword() {
        //noinspection ConstantConditions
        if (MAX_PASSWORD_LENGTH == Integer.MAX_VALUE) {
            return RandomStringUtils.random(MAX_PASSWORD_LENGTH) + "Some bullshit";
        }

        return RandomStringUtils.random(MAX_PASSWORD_LENGTH + 1);
    }

    /**
     * Generates a password with correct length, but missing uppercase letters.
     *
     * @return A password without uppercase letters.
     */
    private static String generateMissingUppercasePassword() {
        return RandomStringUtils.randomPrint(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH).toLowerCase();
    }

    /**
     * Generates a password with correct length, but missing lowercase letters.
     *
     * @return A password without lowercase letters.
     */
    private static String generateMissingLowercasePassword() {
        return RandomStringUtils.randomPrint(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH).toUpperCase();
    }

    /**
     * Generates a password with correct length, but missing numbers.
     *
     * @return A password without numbers.
     */
    private static String generateMissingNumberPassword() {
        return RandomStringUtils.randomAlphabetic(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH);
    }

    /**
     * Generates a password with correct length, but missing special characters.
     *
     * @return A password without special characters.
     */
    private static String generateMissingSpecialCharacterPassword() {
        return RandomStringUtils.randomAlphanumeric(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH);
    }
}
