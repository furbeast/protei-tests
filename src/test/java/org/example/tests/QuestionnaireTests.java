package org.example.tests;

import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.example.entity.Questionnaire;
import org.example.pages.AlertPage;
import org.example.pages.LoginPage;
import org.example.pages.QuestionnairePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Tag("questionnaire")
@DisplayName("Тесты анкеты")
public class QuestionnaireTests extends BaseTest {

    private static final String MESSAGE_EMAIL_FORMAT_ERROR = "Неверный формат E-Mail";
    private static final String MESSAGE_BLANK_NAME_ERROR = "Поле имя не может быть пустым";

    private QuestionnairePage questionnairePage;

    Questionnaire questionnaire;

    @BeforeEach
    void loginAndOpenQuestionnaire() {
        questionnairePage = new LoginPage(driver).loginAs(VALID_EMAIL, VALID_PASS).success();
    }

    @BeforeEach
    void createFakeQuestionnaire() {
        Faker faker = new Faker(Locale.of("ru"));

        questionnaire = new Questionnaire(
                new Faker().internet().emailAddress(),
                faker.name().firstName() + " " + faker.name().lastName(),
                faker.gender().binaryTypes().equals("Male") ? "Мужской" : "Женский",
                faker.bool().bool(),
                faker.bool().bool(),
                faker.options().option("Вариант 2.1", "Вариант 2.2", "Вариант 2.3")
        );
    }

    @Test
    @DisplayName("Добавление записи — появляется alert")
    void addEntryShowAlert() {
        AlertPage alertPage = questionnairePage
                .fillQuestionnaire(questionnaire)
                .success();

        assertTrue(alertPage.isPageLoaded());
        assertEquals("Данные добавлены.", alertPage.getText());
        alertPage.clickOk();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    @DisplayName("Добавление записи — строка появляется в таблице")
    void addEntryAppearsInTable(int count) {
        assertEquals(0, questionnairePage.getRowCount());

        for (int i = 0; i < count; i++) {
            createFakeQuestionnaire();
            AlertPage alertPage = questionnairePage
                    .fillQuestionnaire(questionnaire)
                    .success();

            assertTrue(alertPage.isPageLoaded());
            alertPage.clickOk();
        }
        assertEquals(count, questionnairePage.getRowCount());

        List<String> row = questionnairePage.getLastRowValues();

        var expected = List.of(questionnaire.email(),
                questionnaire.name(),
                questionnaire.gender(),
                getChoice1(),
                getChoice2());
        assertIterableEquals(expected, row);
    }

    @ParameterizedTest
    @DisplayName("Данные последней строки совпадают с введёнными")
    @MethodSource("provideValidQuestionnaireData")
    void lastRowMatchesInput(boolean option11, boolean option12,
                             String option2) {
        questionnaire = new Questionnaire(
                questionnaire.email(),
                questionnaire.name(),
                questionnaire.gender(),
                option11,
                option12,
                option2
        );

        AlertPage alertPage = questionnairePage
                .fillQuestionnaire(questionnaire)
                .success();

        assertTrue(alertPage.isPageLoaded());
        alertPage.clickOk();

        List<String> row = questionnairePage.getLastRowValues();

        var expected = List.of(questionnaire.email(),
                questionnaire.name(),
                questionnaire.gender(),
                getChoice1(),
                getChoice2());
        assertIterableEquals(expected, row);
    }

    @Test
    @DisplayName("Заголовки таблицы соответствуют ожидаемым")
    void tableHeadersAreCorrect() {
        List<String> headers = questionnairePage.getHeaders();

        var expected = List.of("E-Mail", "Имя", "Пол", "Выбор 1", "Выбор 2");
        assertIterableEquals(expected, headers);
    }

    @ParameterizedTest()
    @DisplayName("Неверный креды — отображается ошибка и alert не появляется")
    @MethodSource("provideInvalidQuestionnaireData")
    void wrongData(String email, String name, String message) {
        questionnairePage
                .fillQuestionnaire(new Questionnaire(
                        email,
                        name,
                        questionnaire.gender(),
                        questionnaire.option11(),
                        questionnaire.option12(),
                        questionnaire.option2())
                )
                .error();

        assertTrue(questionnairePage.isDisplayed());
        assertEquals(message, questionnairePage.getErrorMessage());

        AlertPage alertPage = new AlertPage(driver);
        assertFalse(alertPage.isDisplayed());

        assertEquals(0, questionnairePage.getRowCount());

        assertEquals(questionnaire.option11(), questionnairePage.option11IsChecked());
        assertEquals(questionnaire.option12(), questionnairePage.option12IsChecked());
    }

    private static Stream<Arguments> provideValidQuestionnaireData() {
        return Stream.of(
                Arguments.of(false, false, ""),
                Arguments.of(false, true, "Вариант 2.1"),
                Arguments.of(true, false, "Вариант 2.2"),
                Arguments.of(true, true, "Вариант 2.3")
        );
    }

    private static Stream<Arguments> provideInvalidQuestionnaireData() {
        return Stream.of(
                Arguments.of("", "", MESSAGE_EMAIL_FORMAT_ERROR),
                Arguments.of("", "testName", MESSAGE_EMAIL_FORMAT_ERROR),
                Arguments.of("test@test.ts", "", MESSAGE_BLANK_NAME_ERROR)
        );
    }

    private String getChoice1() {
        if (!questionnaire.option11() && !questionnaire.option12())
            return "Нет";
        return (questionnaire.option11() ? "1.1" : "")
                + (questionnaire.option11() && questionnaire.option12() ? ", " : "")
                + (questionnaire.option12() ? "1.2" : "");
    }

    private String getChoice2() {
        return questionnaire.option2().replace("Вариант ", "");
    }
}
