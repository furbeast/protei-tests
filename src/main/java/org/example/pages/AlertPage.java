package org.example.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

public class AlertPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(className = "uk-modal-dialog")
    private List<WebElement> modalDialog;

    @FindBy(className = "uk-modal-content")
    private WebElement text;

    @FindBy(className = "uk-modal-close")
    private WebElement okButton;

    public AlertPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    @Step("Получение текста модального окна")
    public String getText() {
        return text.getText();
    }

    @Step("Нажатие кнопки 'OK' в модальном окне")
    public QuestionnairePage clickOk() {
        okButton.click();
        return new QuestionnairePage(driver);
    }

    @Step("Проверка отображения модального окна")
    public boolean isDisplayed() {
        return !modalDialog.isEmpty();
    }

    @Step("Проверка полной загрузки модального окна")
    public boolean isPageLoaded() {
        try {
            wait.until(visibilityOf(text));
            wait.until(visibilityOf(okButton));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
