package org.example.pages;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Questionnaire;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class QuestionnairePage {

    private final WebDriver driver;
    private final WebDriverWait wait;


    private static final String TABLE_XPATH = "//table[@id='dataTable']";
    private static final By TABLE_ROWS_LOCATOR = By.xpath(TABLE_XPATH + "/tbody/tr");
    private static final By TABLE_HEADERS_LOCATOR = By.xpath(TABLE_XPATH + "/thead/tr");

    @FindBy(id = "dataEmail")
    private WebElement emailInput;

    @FindBy(id = "dataName")
    private WebElement nameInput;

    @FindBy(id = "dataGender")
    private WebElement genderSelect;

    @FindBy(id = "dataCheck11")
    private WebElement checkbox11;

    @FindBy(id = "dataCheck12")
    private WebElement checkbox12;

    @FindBy(id = "dataSelect21")
    private WebElement radio21;

    @FindBy(id = "dataSelect22")
    private WebElement radio22;

    @FindBy(id = "dataSelect23")
    private WebElement radio23;

    @FindBy(id = "dataSend")
    private WebElement addButton;

    @FindBy(xpath = "//*[@id='emailFormatError' or @id='blankNameError']")
    private WebElement errorMessage;


    public QuestionnairePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    @Step("Ввод E-Mail в анкете: {email}")
    public QuestionnairePage enterEmail(String email) {
        emailInput.clear();
        emailInput.sendKeys(email);
        return this;
    }

    @Step("Ввод Имени: {name}")
    public QuestionnairePage enterName(String name) {
        nameInput.clear();
        nameInput.sendKeys(name);
        return this;
    }

    @Step("Выбор пола: {value}")
    public QuestionnairePage selectGender(String value) {
        new Select(genderSelect).selectByVisibleText(value);
        return this;
    }

    @Step("Установка чекбокса 'Вариант 1.1': {select}")
    public QuestionnairePage selectOption11(boolean select) {
        setCheckbox(checkbox11, select);
        return this;
    }

    @Step("Проверка состояния чекбокса 'Вариант 1.1'")
    public boolean option11IsChecked() {
        return checkbox11.isSelected();
    }

    @Step("Установка чекбокса 'Вариант 1.2': {select}")
    public QuestionnairePage selectOption12(boolean select) {
        setCheckbox(checkbox12, select);
        return this;
    }

    @Step("Проверка состояния чекбокса 'Вариант 1.2'")
    public boolean option12IsChecked() {
        return checkbox12.isSelected();
    }

    @Step("Выбор радио-кнопки: {select}")
    public QuestionnairePage selectOptions2(String select) {
        switch (select) {
            case "" -> {
            }
            case "Вариант 2.1" -> setRadio(radio21);
            case "Вариант 2.2" -> setRadio(radio22);
            case "Вариант 2.3" -> setRadio(radio23);
            default -> throw new IllegalArgumentException("Неизвестный вариант: " + select);
        }
        return this;
    }

    @Step("Нажатие кнопки 'Добавить'")
    public QuestionnairePage addEntry() {
        addButton.click();
        return this;
    }

    @Step("Полное заполнение анкеты данными пользователя")
    public QuestionnairePage fillQuestionnaire(Questionnaire questionnaire) {
        enterEmail(questionnaire.email());
        enterName(questionnaire.name());
        selectGender(questionnaire.gender());
        selectOption11(questionnaire.option11());
        selectOption12(questionnaire.option12());
        selectOptions2(questionnaire.option2());
        addEntry();
        return this;
    }

    @Step("Переход к проверке модального окна (успешное добавление)")
    public AlertPage success() {
        return new AlertPage(driver);
    }

    @Step("Ожидание ошибки валидации анкеты")
    public QuestionnairePage error() {
        return this;
    }

    @Step("Получение количества строк в таблице")
    public int getRowCount() {
        return driver.findElements(TABLE_ROWS_LOCATOR).size();
    }

    @Step("Получение значений последней строки таблицы")
    public List<String> getLastRowValues() {
        List<WebElement> currentRows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(TABLE_ROWS_LOCATOR));
        List<WebElement> cells = currentRows.getLast()
                .findElements(By.tagName("td"));
        return cells.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    @Step("Получение заголовков таблицы")
    public List<String> getHeaders() {
        WebElement headers = wait.until(ExpectedConditions.presenceOfElementLocated(TABLE_HEADERS_LOCATOR));
        return headers.findElements(By.tagName("th")).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    @Step("Получение текста ошибки валидации анкеты")
    public String getErrorMessage() {
        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        return errorMessage.getText();
    }

    @Step("Проверка загрузки страницы анкеты")
    public boolean isPageLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOf(addButton));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    @Step("Проверка отображения формы анкеты")
    public boolean isDisplayed() {
        return emailInput.isDisplayed()
                && addButton.isDisplayed();
    }

    private void setCheckbox(WebElement checkbox, boolean select) {
        if (checkbox.isSelected() != select)
            checkbox.click();
    }

    private void setRadio(WebElement radio) {
        if (!radio.isSelected())
            radio.click();
    }
}
