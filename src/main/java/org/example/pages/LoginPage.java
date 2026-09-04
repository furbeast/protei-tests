package org.example.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;


    @FindBy(id = "loginEmail")
    private WebElement emailInput;

    @FindBy(id = "loginPassword")
    private WebElement passwordInput;

    @FindBy(id = "authButton")
    private WebElement loginButton;

    @FindBy(xpath = "//*[@id='emailFormatError' or @id='invalidEmailPassword']")
    private WebElement errorMessage;

    @FindBy(id = "loginForm")
    private WebElement loginForm;


    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    @Step("Ввод E-Mail: {email}")
    public LoginPage enterEmail(String email) {
        emailInput.clear();
        emailInput.sendKeys(email);
        return this;
    }

    @Step("Ввод пароля")
    public LoginPage enterPassword(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);
        return this;
    }

    @Step("Нажатие кнопки 'Вход'")
    public LoginPage clickLogin() {
        loginButton.click();
        return this;
    }

    @Step("Получение текста ошибки авторизации")
    public String getErrorMessage() {
        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        return errorMessage.getText();
    }

    @Step("Проверка отображения формы логина")
    public boolean isDisplayed() {
        return emailInput.isDisplayed()
                && passwordInput.isDisplayed()
                && loginButton.isDisplayed();
    }

    @Step("Выполнение авторизации с логином: {email}")
    public LoginPage loginAs(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
        return this;
    }

    @Step("Переход к анкете (успешный вход)")
    public QuestionnairePage success() {
        return new QuestionnairePage(driver);
    }

    @Step("Ожидание ошибки авторизации")
    public LoginPage error() {
        return this;
    }
}
