package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class HomeTests {
    @LocalServerPort
    private Integer port;
    private final String USERNAME = "name0101";
    private final String PASSWORD = "0000password";

    private static WebDriver driver;
    private HomePageObject homePage;
    private LoginPageObject loginPage;
    private SignupPageObject signupPage;
    private ResultPageObject resultPage;
    private final String webx = "http://localhost:";



    @BeforeAll
    public static void beforeAll() {
    }

    @AfterAll
    public static void afterAll() {
    }

    @BeforeEach
    public void beforeEach() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("http://localhost:" + port + "/login");
        loginPage = new LoginPageObject(driver);
    }

    @AfterEach
    public void afterEach() {
        driver.quit();
    }

    @Test
    public void homePageNotAccess1LoggedIn() {
        getHomePage();
        String currentURL = driver.getCurrentUrl();
        assertTrue(currentURL.indexOf("home") == -1);
    }

    @Test
    public void signUpLogInVerifyAccessHomePage() throws InterruptedException {
        signUpAndLogin(USERNAME, PASSWORD);
        String currentURL = driver.getCurrentUrl();
        assertTrue(currentURL.indexOf("home") >= 0);
        getHomePage();
        homePage.getLogOutButton().click();
        currentURL = driver.getCurrentUrl();
        assertTrue(currentURL.indexOf("login") >= 0);
        driver.get(webx + port + "/home");
        currentURL = driver.getCurrentUrl();
        assertTrue(currentURL.indexOf("login") >= 0);
    }

    @Test
    public void createNoteAndSeeInListTest() throws InterruptedException {
        signUpAndLogin(USERNAME, PASSWORD);
        createNoteInList();
        Thread.sleep(1000);
        List<WebElement> allTitles = driver.findElements(By.id("noteTitleList"));
        for (WebElement title: allTitles) {
            String theTitle = title.getText();
            assertEquals("This is the title", theTitle);
        }
    }

    @Test
    public void editNoteTest() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait (driver, 500);
        signUpAndLogin(USERNAME, PASSWORD);
        createNoteInList();
        logout(wait);
        Thread.sleep(3000);
        login(USERNAME, PASSWORD);
        Thread.sleep(3000);
        waitAndClick(wait, homePage.getNavNotesTab());
        Thread.sleep(3000);
        List<WebElement> allEditNote = driver.findElements(By.id("editNote"));
        WebElement firstEditButton = allEditNote.get(0);
        waitAndClick(wait, firstEditButton);
        waitOn(wait, homePage.getNoteTitle());
        homePage.getNoteTitle().clear();
        waitAndAddText(wait, homePage.getNoteTitle(), "title 1");
        waitAndClick(wait, homePage.getSubmitNoteButtonFooter());
        Thread.sleep(3000);
        waitAndClick(wait, homePage.getSaveContinueOnResult());
        Thread.sleep(1000);
        waitAndClick(wait, homePage.getNavNotesTab());
 
        Thread.sleep(1000);
        List<WebElement> allTitles = driver.findElements(By.id("noteTitleList"));
        for (WebElement title: allTitles) {
            String theTitle = title.getText();
            assertEquals("title 1", theTitle);
        }
    }

    @Test
    public void deleteNotetTest() throws Exception {
        signUpAndLogin(USERNAME, PASSWORD);
        getHomePage();
        WebDriverWait wait = new WebDriverWait (driver, 500);
        Thread.sleep(3000);
        createNewNoteNavBackToNotesTab(wait, "Note 1", " note description 1");
        createNewNoteNavBackToNotesTab(wait, "Note 2", "note description 2");
        logout(wait);
        Thread.sleep(3000);
        login(USERNAME, PASSWORD);
        Thread.sleep(3000);
        waitAndClick(wait, homePage.getNavNotesTab());
        Thread.sleep(3000);
        List<WebElement> allDeleteNoteButtons = driver.findElements(By.id("deleteNote"));
        int originalSizeBeforeDelete = allDeleteNoteButtons.size();
        WebElement firstDeleteButton = allDeleteNoteButtons.get(0);
        waitAndClick(wait, firstDeleteButton);
        Thread.sleep(3000);
        waitAndClick(wait, homePage.getSaveContinueOnResult());
        Thread.sleep(1000);
        waitAndClick(wait, homePage.getNavNotesTab());
     
        Thread.sleep(3000);
        List<WebElement> allTitles = driver.findElements(By.id("noteTitleList"));
        assertTrue((originalSizeBeforeDelete == (allTitles.size() + 1)));
        String title = allTitles.get(0).getText();
        assertEquals("Note 2", title);
    }

    @Test
    public void createCredentialTest() throws InterruptedException {
        signUpAndLogin(USERNAME, PASSWORD);
        createCredentialAndSeeInList();

                Thread.sleep(1000);
        List<WebElement> allUrls = driver.findElements(By.id("credentailURLList"));
        for (WebElement url: allUrls) {
            String theUrl = url.getText();
            assertEquals("http://blah.com", theUrl);
        }
    }

    @Test
    public void editCredentialTest() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait (driver, 500);
        signUpAndLogin(USERNAME, PASSWORD);
        createCredentialAndSeeInList();
        logout(wait);
        Thread.sleep(3000);
        login(USERNAME, PASSWORD);
        Thread.sleep(3000);
        waitAndClick(wait, homePage.getNavCredentialsTab());
        Thread.sleep(3000);
        List<WebElement> allEditCredential = driver.findElements(By.id("editCredential"));
        WebElement firstEditButton = allEditCredential.get(0);
        waitAndClick(wait, firstEditButton);
        waitOn(wait, homePage.getCredentialUrl());
        homePage.getCredentialUrl().clear();
        waitAndAddText(wait, homePage.getCredentialUrl(), "http://facebook.com");
        waitAndClick(wait, homePage.getSaveCredentialFooter());
        Thread.sleep(3000);
        waitAndClick(wait, homePage.getSaveContinueOnResult());
        Thread.sleep(1000);
        waitAndClick(wait, homePage.getNavCredentialsTab());
        Thread.sleep(1000);
        List<WebElement> allURL = driver.findElements(By.id("credentailURLList"));
        for (WebElement url: allURL) {
            String theTitle = url.getText();
            assertEquals("http://facebook.com", theTitle);
        }
    }

    @Test
    public void deleteCredentiaTest() throws Exception {
        signUpAndLogin(USERNAME, PASSWORD);
        getHomePage();
        WebDriverWait wait = new WebDriverWait (driver, 500);
        Thread.sleep(3000);
        createNewCredentialNavBackToNotesTab(wait, "http://ebuy.com", "user010101", "password");
        createNewCredentialNavBackToNotesTab(wait, "http://facebook.com", "James", "password 00");
        logout(wait);
        Thread.sleep(3000);
        login(USERNAME, PASSWORD);
        Thread.sleep(3000);
        waitAndClick(wait, homePage.getNavCredentialsTab());
        Thread.sleep(3000);
        List<WebElement> allDeleteCredentialButtons = driver.findElements(By.id("deleteCredential"));
        int originalSizeBeforeDelete = allDeleteCredentialButtons.size();
        WebElement firstDeleteButton = allDeleteCredentialButtons.get(0);
        waitAndClick(wait, firstDeleteButton);
        Thread.sleep(3000);
        waitAndClick(wait, homePage.getSaveContinueOnResult());
        Thread.sleep(1000);
        waitAndClick(wait, homePage.getNavCredentialsTab());

        Thread.sleep(3000);
        List<WebElement> credentailURLList = driver.findElements(By.id("credentailURLList"));
        assertTrue((originalSizeBeforeDelete == (credentailURLList.size() + 1)));
        String url = credentailURLList.get(0).getText();
        assertEquals("http://facebook.com", url);
    }


    private void createCredentialAndSeeInList() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait (driver, 500);
        getHomePage();

        createNewCredentialNavBackToCredentialsTab(wait, "http://wbname.com", "nameuserbnb", "password");
    }

    private void createNewCredentialNavBackToCredentialsTab(WebDriverWait wait, String url, String username, String password) throws InterruptedException {
        waitAndClick(wait, homePage.getNavCredentialsTab());
        waitAndClick(wait, homePage.getShowCredentialsModal());
        waitAndAddText(wait, homePage.getCredentialUrl(), url);
        waitAndAddText(wait, homePage.getCredentialUsername(), username);
        waitAndAddText(wait, homePage.getCredentialPassword(), password);
        waitAndClick(wait, homePage.getSaveCredentialFooter());
        Thread.sleep(3000);
        waitAndClick(wait, homePage.getSaveContinueOnResult());
        Thread.sleep(3000);
        waitAndClick(wait, homePage.getNavCredentialsTab());
    }

    private void signUpAndLogin(String userName, String passWord) throws InterruptedException {
        signupPage = new SignupPageObject(driver);
        driver.get(webx + port + "/signup");

        SignupPageObject signupPage = new SignupPageObject(driver);
        signupPage.signup("nameuser", "password", userName, passWord);
        signupPage.getSubmitButton().click();

        login(userName, passWord);
    }

    private void getHomePage() {
        driver.get(webx + port + "/home");
        homePage = new HomePageObject(driver);
    }

    private void createNoteInList() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait (driver, 500);
        getHomePage();

        createNewNoteNavBackToNotesTab(wait, "the title", "the description");
    }

    private void createNewNoteNavBackToNotesTab(WebDriverWait wait, String title, String description) throws InterruptedException {
        waitAndClick(wait, homePage.getNavNotesTab());
        waitAndClick(wait, homePage.getShowNoteModelButton());
        waitAndAddText(wait, homePage.getNoteTitle(), title);
        waitAndAddText(wait, homePage.getNoteDescription(), description);
        waitAndClick(wait, homePage.getSubmitNoteButtonFooter());
        Thread.sleep(3000);
        waitAndClick(wait, homePage.getSaveContinueOnResult());
        Thread.sleep(1000);
        waitAndClick(wait, homePage.getNavNotesTab());
    }

    private void createNewCredentialNavBackToNotesTab(WebDriverWait wait, String url, String username, String password) throws InterruptedException {
        waitAndClick(wait, homePage.getNavCredentialsTab());
        waitAndClick(wait, homePage.getShowCredentialsModal());
        waitAndAddText(wait, homePage.getCredentialUrl(), url);
        waitAndAddText(wait, homePage.getCredentialUsername(), username);
        waitAndAddText(wait, homePage.getCredentialPassword(), password);
        waitAndClick(wait, homePage.getSaveCredentialFooter());
        Thread.sleep(3000);
        waitAndClick(wait, homePage.getSaveContinueOnResult());
        Thread.sleep(1000);
        waitAndClick(wait, homePage.getNavNotesTab());
    }

    private void waitAndClick(WebDriverWait wait, WebElement webElement) {
        wait.until(ExpectedConditions.elementToBeClickable(webElement)).click();
    }

    private void waitOn(WebDriverWait wait, WebElement webElement) {
        wait.until(ExpectedConditions.elementToBeClickable(webElement));
    }

    private void waitAndAddText(WebDriverWait wait, WebElement webElement, String textToAdd) {
        wait.until(ExpectedConditions.elementToBeClickable(webElement));
        sendKeys(webElement, textToAdd);
    }

    private void sendKeys(final WebElement element, final String keys) {
        for (var i = 0; i < keys.length(); i++) {
            element.sendKeys(String.valueOf(keys.charAt(i)));
        }
    }

    private void logout(WebDriverWait wait) throws InterruptedException {
        Thread.sleep(5000);
        WebElement logoutButton = homePage.getLogOutButton();
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton)).click();
    }

    private void login(String username, String password) {
        driver.get(webx + port + "/login");
        LoginPageObject loginPage = new LoginPageObject(driver);
        loginPage.login(username, password);
        loginPage.getSubmitButton().click();
    }
}


