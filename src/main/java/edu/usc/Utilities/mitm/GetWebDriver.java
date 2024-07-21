package edu.usc.Utilities.mitm;

import edu.usc.Utilities.LoadConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

import java.io.File;

public class GetWebDriver {

    WebDriver refDriver;
    int proxyPort;
    MitmproxyJava mitmproxy;

    public GetWebDriver(String subject, String url, LoadConfig config){
        this.proxyPort = 9998;
        String mitmproxyPath = config.GetmitmPath() + File.separator + "mitmdump";
        String cachePath = config.GetSubjectPath(subject);
        mitmproxy = new MitmproxyJava(mitmproxyPath, cachePath, proxyPort);
        try {
            mitmproxy.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.refDriver = SetUpWebDriver(proxyPort);
        refDriver.get(url);
    }

    public WebDriver SetUpWebDriver(int proxyPort){
        Boolean headless = true;
        Proxy proxy = new Proxy();
        proxy.setHttpProxy("localhost:" + proxyPort);
        proxy.setSslProxy("localhost:" + proxyPort);
        ChromeOptions option = new ChromeOptions();
        option.addArguments("--disable-web-security");
        option.addArguments("--allow-running-insecure-content");
        option.addArguments("--disable-gpu");
        option.addArguments("--ignore-certificate-errors");
        option.addArguments("--disable-extensions");
        option.addArguments("--no-sandbox");
        option.addArguments("--disable-dev-shm-usage");
        option.setCapability(CapabilityType.PROXY, proxy);
        if (headless) {
            option.addArguments("--headless"); // Enable headless mode
        }
        WebDriverManager.chromedriver().setup();
        ChromeDriver driver = new ChromeDriver(option);
        driver.manage().window().maximize();
        return driver;
    }

    public void shutdownWebDriver() {
        refDriver.quit();
    }

}
