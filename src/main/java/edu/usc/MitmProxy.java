package edu.usc;
import java.util.Properties;
package edu.usc.webproxy;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import java.io.File;
import java.util.List;

import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class MitmProxy {

    public void RunSubject(Properties Configs){
        String firefox_location = Configs.getProperty("firefox_driver_location");
        String firefox_version = Configs.getProperty("use_firefox_version");
        String mitm_version = Configs.getProperty("use_mitmproxy_version");


        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");


    }
}
