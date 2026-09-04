package org.example.tests;

import org.example.pages.LoginPage;
import org.example.pages.QuestionnairePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@Tag("login")
@DisplayName("Тесты формы авторизации")
public class LoginTests extends BaseTest {

    private static final String MESSAGE_EMAIL_FORMAT_ERROR = "Неверный формат E-Mail";
    private static final String MESSAGE_INVALID_EMAIL_PASSWORD = "Неверный E-Mail или пароль";


    @Test
    @DisplayName("Успешный вход открывает анкету")
    void successfulLogin() {
        QuestionnairePage questionnairePage = new LoginPage(driver)
                .loginAs(VALID_EMAIL, VALID_PASS)
                .success();
        assertTrue(questionnairePage.isPageLoaded());
    }

    @ParameterizedTest()
    @DisplayName("Неверный креды — отображается ошибка и остаёмся на форме логина")
    @MethodSource("provideInvalidAuthData")
    void wrongCredentials(String email, String pass, String message) {
        LoginPage loginPage = new LoginPage(driver)
                .loginAs(email, pass)
                .error();

        assertTrue(loginPage.isDisplayed());
        assertEquals(message, loginPage.getErrorMessage());

        QuestionnairePage questionnairePage = new QuestionnairePage(driver);
        assertFalse(questionnairePage.isDisplayed());
    }


    static Stream<Arguments> provideInvalidAuthData() {
        return Stream.of(
                Arguments.of("", "", MESSAGE_EMAIL_FORMAT_ERROR),
                Arguments.of("wrongEmail", "wrongPass", MESSAGE_EMAIL_FORMAT_ERROR),
                Arguments.of(VALID_EMAIL, "wrongPass", MESSAGE_INVALID_EMAIL_PASSWORD),
                Arguments.of("wrong@protei.ru", VALID_PASS, MESSAGE_INVALID_EMAIL_PASSWORD)
        );
    }
}
