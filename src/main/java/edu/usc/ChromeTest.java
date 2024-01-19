package edu.usc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
public class ChromeTest {
    void main() {
        String sutUrl = "https://bonigarcia.dev/selenium-webdriver-java/";
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.get(sutUrl);
    }

}
