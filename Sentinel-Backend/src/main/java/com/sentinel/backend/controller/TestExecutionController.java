package com.sentinel.backend.controller;

import com.sentinel.backend.entity.TestResult;
import com.sentinel.backend.repository.TestResultRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*; // Updated imports

import java.time.LocalDateTime;

@RestController
public class TestExecutionController {

    @Autowired
    private TestResultRepository repository;

    // 1. Create a "Package" to hold incoming data
    public static class TestRequest {
        public String url;
        public String testType; // "LOGIN_FLOW" or "LOAD_CHECK"
    }

    @PostMapping("/api/run-test")
    public String triggerTest(@RequestBody TestRequest request) { // Read the package
        
        String targetUrl = (request.url != null && !request.url.isEmpty()) ? request.url : "https://www.saucedemo.com/";
        System.out.println("🚀 Starting Test: " + request.testType + " on " + targetUrl);

        TestResult result = new TestResult();
        result.setTestName(request.testType + ": " + targetUrl);
        result.setTimestamp(LocalDateTime.now());

        WebDriver driver = null;
        long startTime = System.currentTimeMillis();

        try {
            // Standard Debian/Chromium Setup
            System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
            ChromeOptions options = new ChromeOptions();
            options.setBinary("/usr/bin/chromium");
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-gpu");

            driver = new ChromeDriver(options);
            
            // --- DYNAMIC LOGIC SWITCH ---
            if ("LOGIN_FLOW".equals(request.testType)) {
                // RUN OLD SWAGLABS TEST
                driver.get("https://www.saucedemo.com/");
                driver.findElement(By.id("user-name")).sendKeys("standard_user");
                driver.findElement(By.id("password")).sendKeys("secret_sauce");
                driver.findElement(By.id("login-button")).click();

                if (driver.getCurrentUrl().contains("inventory.html")) {
                    result.setStatus("PASS");
                    result.setErrorMessage("SwagLabs Login Successful.");
                } else {
                    result.setStatus("FAIL");
                    result.setErrorMessage("Login Failed.");
                }

            } else {
                // RUN GENERIC LOAD CHECK (For any website)
                driver.get(targetUrl);
                String title = driver.getTitle();
                
                if (title != null && !title.isEmpty()) {
                    result.setStatus("PASS");
                    result.setErrorMessage("Site Loaded. Title: " + title);
                } else {
                    result.setStatus("FAIL");
                    result.setErrorMessage("Site failed to load title.");
                }
            }
            // -----------------------------

        } catch (Exception e) {
            result.setStatus("FAIL");
            result.setErrorMessage("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            long endTime = System.currentTimeMillis();
            result.setDurationMs(endTime - startTime);
            if (driver != null) driver.quit();
            
            repository.save(result);
        }

        return "Execution Complete";
    }
}