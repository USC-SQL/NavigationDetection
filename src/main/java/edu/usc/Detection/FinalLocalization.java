package edu.usc.Detection;

import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphNode;
import edu.usc.Utilities.LoadConfig;
import edu.usc.Utilities.mitm.GetWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class FinalLocalization {

    public static void FindFaultyElements(Set<UIGraphEdge> LNF_failures, LoadConfig configs, String subject) throws Exception {
        Set<String> fault_locations = new HashSet<>(); //set of xpaths
        GetWebDriver WebDriverObj = new GetWebDriver(subject, configs.getSubjectURL(subject), configs);
        TimeUnit.SECONDS.sleep(10);
        WebDriver refDriver = WebDriverObj.getWebDriver();
        for(UIGraphEdge edge: LNF_failures){
            boolean tabindex = false;
            String vertexOne_xpath = edge.getV1().getXpath();
            String vertexTwo_xpath = edge.getV2().getXpath();
            WebElement vertexOne = refDriver.findElement(By.xpath(vertexOne_xpath));
            WebElement vertexTwo = refDriver.findElement(By.xpath(vertexTwo_xpath));
            String vertexOne_tabindex = vertexOne.getAttribute("tabindex");
            String vertexTwo_tabindex = vertexTwo.getAttribute("tabindex");
            try {
                if (!vertexOne_tabindex.equals("0")) {
                    fault_locations.add(vertexOne_xpath);
                    tabindex = true;
                }
            } catch (NullPointerException e){
                //No tabindex values
            }
            try {
                if (!vertexTwo_tabindex.equals("0")) {
                    fault_locations.add(vertexTwo_xpath);
                    tabindex = true;
                }
            } catch (NullPointerException e){
                //No tabindex values
            }
            if(tabindex){
                continue;
            }
            if(fault_locations.contains(vertexOne_xpath)){
                continue;
            } else {
                fault_locations.add(vertexTwo_xpath);
            }
            //add code here to check event listener
        }
        WebDriverObj.shutdownWebDriver();
        WebDriverObj.shutdownMitmProxy();
        for(String xpath: fault_locations){
            System.out.println(xpath);
        }
    }

}
