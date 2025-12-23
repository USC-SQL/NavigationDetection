package edu.usc.Utilities.LNFValidation;

import edu.usc.ContextTree.ContextTree;
import edu.usc.Utilities.ReadInJSResource;
import edu.usc.KFG.UIGraph.UIGraphEdge;
import edu.usc.KFG.UIGraph.UIGraphState;
import edu.usc.Utilities.LoadConfig;
import edu.usc.Utilities.mitm.GetWebDriver;
import edu.usc.ContextTree.Construction.AffinityCalculation.GetAttributeData;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static edu.usc.KFG.KFGUtilities.UtilityFunctions.LoadTheKFG;

///////////////////////
// This class is used to label all the keyboard navigable elements in the PUT.
// It is used as a part of the validation aspect of the approach by having volunteers rate
// if a given sequence (e.g., 1 - > 2) violates WCAG 2.4.3
///////////////////////
public class DrawLNFLabels {

    public static void DrawLabels(String subject, LoadConfig configs_obj) throws InterruptedException, IOException {
        UIGraphState KFG = LoadTheKFG(configs_obj, subject);
        ContextTree CTree = new ContextTree();
        CTree.Load(configs_obj, subject);
        ReadInJSResource obj = new ReadInJSResource();
        String script = obj.read(obj, "drawLabels.js");

        GetWebDriver WebDriverObj = new GetWebDriver(subject, configs_obj.getSubjectURL(subject), configs_obj);
        TimeUnit.SECONDS.sleep(10);
        WebDriver refDriver = WebDriverObj.getWebDriver();

        GetAttributeData attribute_obj = new GetAttributeData();
        WebElement start_ele = refDriver.findElement(By.xpath(KFG.getV_entry().getXpath()));
        ArrayList start_ele_data = attribute_obj.AttributeData(refDriver, start_ele);
        List<Integer> start_mbr = (List<Integer>) start_ele_data.get(3);

        //FunctionalArea start = CTree.FindByXpath(KFG.getV_entry().getXpath());
        //List<Integer> start_mbr = start.getMBR();
        int y_coor = start_mbr.get(1) - 10;
        int x_coor = start_mbr.get(0);
        ((JavascriptExecutor) refDriver).executeScript(script, String.valueOf(x_coor) + "px",  String.valueOf(y_coor) + "px", "1");

        System.out.println("1");
        System.out.println(NormalizeXpath(KFG.getV_entry().getXpath()));
        System.out.println(start_mbr);
        System.out.println();

        List<UIGraphEdge> nav_order = KFG.getOrder();
        int count = 2;
        for(UIGraphEdge edge: nav_order){
            String target_xpath = edge.getV2().getXpath();
            WebElement ele = refDriver.findElement(By.xpath(target_xpath));
            ArrayList ele_data = attribute_obj.AttributeData(refDriver, ele);
            List<Integer> target_mbr = (List<Integer>) ele_data.get(3);
            String xpath = NormalizeXpath(target_xpath);

            System.out.println(count);
            System.out.println(xpath);
            System.out.println(target_mbr);
            System.out.println();

            y_coor = target_mbr.get(1) - 10;
            x_coor = target_mbr.get(0);
            ((JavascriptExecutor) refDriver).executeScript(script, String.valueOf(x_coor) + "px", String.valueOf(y_coor) + "px", String.valueOf(count));
            count++;
        }
        System.out.println("Total elements: " + count);
        //((JavascriptExecutor) refDriver).executeScript(script, "300px", "300px", "12");
    }

    public static String NormalizeXpath(String input_xpath) {
        String xpath = input_xpath.toLowerCase();
        int k = 0;

        while (k < xpath.length()) {
            if (k > 0) {
                String subString = xpath.substring(k - 1, k + 1);

                if (subString.charAt(1) == '/' && subString.charAt(0) != ']') {
                    String newSubString = subString.substring(0, 1) + "[1]/";
                    xpath = xpath.replace(subString, newSubString);
                    k = k - 3; // Go back 3 steps after replacing
                }
            }
            k++;
        }
        if(xpath.charAt(xpath.length() - 1) != ']'){
            xpath = xpath + "[1]";
        }
        return xpath;
    }

}
