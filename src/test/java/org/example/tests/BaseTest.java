package org.example.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    private static final String PAGE_URL = "file:///" + System.getProperty("user.dir").replace('\\', '/')
            + "/src/test/resources/qa-test.html";
    protected static final String VALID_EMAIL = System.getProperty("TEST_EMAIL", "test@protei.ru");
    protected static final String VALID_PASS = System.getProperty("TEST_PASSWORD", "test");

    @BeforeAll
    static void setupDriverManager() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(PAGE_URL);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }
}
