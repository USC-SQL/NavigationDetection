package edu.usc.Clustering;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.ArrayList;
import java.util.List;

public class GetElements {

    static WebDriver driver;
    public static List<WebElement> AllVisibleElements(String URL) {
        String PageUrl = URL;
        //PageUrl = "https://bonigarcia.dev/selenium-webdriver-java/";
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get(PageUrl);

        List<WebElement> elements = driver.findElements(By.cssSelector("*"));
        List<WebElement> visibleElements = new ArrayList<WebElement>();
        for(WebElement elem : elements){
            if(elem.isDisplayed()){
                visibleElements.add(elem);
            }
        }

        System.out.println("Total Elements: " + elements.size());
        System.out.println("Total Visible Elements " + visibleElements.size());
        return visibleElements;
    }

    public static void closedown(){
        driver.quit();
    }

}
