package edu.usc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChromeTest {
    public static void main(String args[]) throws IOException {
        String sutUrl = "https://bonigarcia.dev/selenium-webdriver-java/";
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.get(sutUrl);

        while(true) {
            System.out.println("Enter 'x' to exit.");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            if (input.equals("x")) {
                System.out.println("Goodbye.");
                driver.quit();
            }
            return;
        }
    }

}
