package com.sentinel.backend.service;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Service;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class SeleniumExecutionService {

    
    private static final String GRID_URL = "http://selenium-hub:4444/wd/hub";

    public String executeTest(String testName) {
        System.out.println(">>> SERVICE: Preparing to run " + testName + " on Selenium Grid...");
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Optional: Run without UI (faster)
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = null;

        try {

            driver = new RemoteWebDriver(new URL(GRID_URL), options);

            System.out.println(">>> SERVICE: Browser Started. Navigating to Google...");
            driver.get("https://www.google.com");
            
            String title = driver.getTitle();
            System.out.println(">>> SERVICE: Title Captured: " + title);
            
            return "SUCCESS: Executed " + testName + " | Page Title: " + title;

        } catch (MalformedURLException e) {
            return "ERROR: Invalid Grid URL -> " + e.getMessage();
        } catch (Exception e) {
            return "FAILURE: Test Crashed -> " + e.getMessage();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}