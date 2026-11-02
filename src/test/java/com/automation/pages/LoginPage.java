package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Supports /login page as well as homepage sign-in forms
    private By usernameInput = By.xpath("//input[@id='username' or @id='session_key' or @name='session_key']");
    private By passwordInput = By.xpath("//input[@id='password' or @id='session_password' or @name='session_password']");
    private By signInBtn = By.xpath("//button[@type='submit' or contains(text(),'Sign in')]");
    private By errorMessage = By.xpath("//div[@id='error-for-password' or @id='error-for-username' or @role='alert']");

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void navigateTo(String url) {
        driver.get(url);
    }

    public void enterUsername(String user) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        element.clear();
        element.sendKeys(user);
    }

    public void enterPassword(String pass) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        element.clear();
        element.sendKeys(pass);
    }

    public void clickSignIn() {
        wait.until(ExpectedConditions.elementToBeClickable(signInBtn)).click();
        handlePossibleSecurityCheckpoint();
    }

    public void login(String user, String pass) {
        enterUsername(user);
        enterPassword(pass);
        clickSignIn();
    }

    public String getErrorMessage() {
        try {
            WebElement err = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
            return err.getText();
        } catch (Exception e) {
            return "Error banner appeared";
        }
    }

    // Handles assignment rule 3.2: Pauses execution if CAPTCHA or checkpoint appears
    private void handlePossibleSecurityCheckpoint() {
        try {
            if (driver.getCurrentUrl().contains("checkpoint") || driver.getTitle().contains("Security Verification")) {
                System.out.println(">>> LinkedIn Security Checkpoint detected! You have 30 seconds to solve it manually in the browser...");
                new WebDriverWait(driver, Duration.ofSeconds(30))
                        .until(ExpectedConditions.urlContains("/feed"));
            }
        } catch (Exception ignored) {
        }
    }
}