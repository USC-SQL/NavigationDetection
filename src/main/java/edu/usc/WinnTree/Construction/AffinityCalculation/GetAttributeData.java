package edu.usc.WinnTree.Construction.AffinityCalculation;

import edu.usc.WinnTree.FunctionalArea;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Point;

import java.util.*;

public class GetAttributeData {

    public ArrayList AttributeData (WebDriver refDriver, WebElement web_ele){
        ArrayList ele_data = new ArrayList();

        String id = web_ele.getAttribute("id");
        String name = web_ele.getAttribute("name");
        String text = web_ele.getText();
        ele_data.add(TransformString(id));
        ele_data.add(TransformString(name));
        ele_data.add(TransformString(text));

        Point point = web_ele.getLocation();
        int TOP_LEFT_X = point.getX();
        int TOP_LEFT_Y = point.getY();
        int BOTTOM_RIGHT_X = point.getX() + web_ele.getSize().getWidth();
        int BOTTOM_RIGHT_Y = point.getY() + web_ele.getSize().getHeight();


        List<Integer> MBR = new ArrayList<Integer>();
        MBR.add(TOP_LEFT_X);
        MBR.add(TOP_LEFT_Y);
        MBR.add(BOTTOM_RIGHT_X);
        MBR.add(BOTTOM_RIGHT_Y);

        float midX = (TOP_LEFT_X + BOTTOM_RIGHT_X)/2;
        float midY = (TOP_LEFT_Y + BOTTOM_RIGHT_Y)/2;
        List<Float> centroid = new ArrayList<Float>();
        centroid.add(midX);
        centroid.add(midY);

        ele_data.add(MBR);
        ele_data.add(centroid);
        return ele_data;
    }

    public HashSet getAppliedCSS(WebDriver refDriver, WebElement web_ele){
        JavascriptExecutor executor = (JavascriptExecutor) refDriver;
        String script = "var s = '';" +
                "var o = getComputedStyle(arguments[0]);" +
                "for(var i = 0; i < o.length; i++){" +
                "s+=o[i] + ':' + o.getPropertyValue(o[i])+';';}" +
                "return s;";
        String css_string = (String) executor.executeScript(script, web_ele);
        List<String> css_list = new ArrayList<String>(Arrays.asList(css_string.split(";")));
        HashSet<String> css_set = new HashSet<String>(css_list);
        return css_set;
    }

    public void FilterCSS(Set work_set){
        //Finds all CSS attributes common to all keyboard elements
        HashSet<FunctionalArea> final_css_set = new HashSet<>();
        Iterator<FunctionalArea> setIterator = work_set.iterator();
        while(setIterator.hasNext()){
            FunctionalArea FA = setIterator.next();
            HashSet<FunctionalArea> FA_css = FA.getApplied_css();
            if(final_css_set.isEmpty()) {
                final_css_set = FA_css;
            } else {
                final_css_set.retainAll(FA_css);
            }
        }
        //Removes all CSS attributes that are applied to all elements
        while(setIterator.hasNext()){
            FunctionalArea FA = setIterator.next();
            HashSet<FunctionalArea> FA_css = FA.getApplied_css();
            FA_css.removeAll(final_css_set);
        }
    }

    public String TransformString(String AString){
        if(AString.equals("")){
            return AString;
        }
        if(CheckCamelCase(AString)){
            String fixed_string = SplitCamelCase(AString);
            AString = fixed_string;
        }
        return AString;
    }

    public boolean CheckCamelCase(String AString){
        String camelCasePattern = "(?:[A-Z])(?:\\S?)+(?:[A-Z])(?:[a-z])+";
        String camelCasePattern2 = "([a-z]+[A-Z]+\\w+)+"; // 3rd edit, getting better
        return AString.matches(camelCasePattern) || AString.matches(camelCasePattern2);
    }

    public String SplitCamelCase(String AString){
        String new_string = "";
        for (String w : AString.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
            new_string += w;
            new_string += " ";
        }
        return new_string;
    }

}
